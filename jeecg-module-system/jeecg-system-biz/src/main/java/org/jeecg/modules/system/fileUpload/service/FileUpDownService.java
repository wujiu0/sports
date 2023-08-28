package org.jeecg.modules.system.fileUpload.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.system.fileUpload.config.FileConfig;
import org.jeecg.modules.system.fileUpload.entity.MultipartFileParam;
import org.jeecg.modules.system.fileUpload.entity.SysChunkRecord;
import org.jeecg.modules.system.fileUpload.entity.SysFile;
import org.jeecg.modules.system.fileUpload.entity.UploadResultVo;
import org.jeecg.modules.system.fileUpload.exception.CustomException;
import org.jeecg.modules.system.fileUpload.util.MsgConstant;
import org.jeecg.modules.system.mapper.SysChunkRecordMapper;
import org.jeecg.modules.system.mapper.SysFileMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.Cleaner;

import javax.annotation.Resource;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FileUpDownService {

    @Resource
    private FileConfig fileConfig;

    @Resource
    private SysFileMapper sysFileMapper;

    @Resource
    private SysChunkRecordMapper sysChunkRecordMapper;

    @Value("${file.chunk_size}")
    private Integer chunk_size;

    /**
     * 分片上传
     */
    public UploadResultVo chunkUpload(MultipartFileParam param) {
        UploadResultVo vo = new UploadResultVo();

        // 因为表设置了唯一约束，在插入数据前先判断数据是否已经存在
        SysFile sf = queryByMd5(param.getMd5());
        if (ObjectUtil.isNotEmpty(sf)) {
            return vo.setUploaded(true).setFileKid(sf.getKid());
        }
        SysChunkRecord scr = queryByChunkMd5(param.getChunkMd5());
        if (ObjectUtil.isNotEmpty(scr)) {
            return vo.setUploaded(false);
        }

        File file = buildUploadFile(param);
        MultipartFile multipartFile = param.getFile();
        try {
            // 分片文件md5校验
            checkMd5(multipartFile.getInputStream(), param.getChunkMd5());
            // 分片数据写入文件
            RandomAccessFile accessFile = new RandomAccessFile(file, "rw");
            if (accessFile.length() == 0) {
                accessFile.setLength(param.getTotalSize());
            }
            FileChannel channel = accessFile.getChannel();
            int position = (param.getChunk() - 1) * fileConfig.getChunkSize();
            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, position, multipartFile.getSize());
            map.put(multipartFile.getBytes());
            cleanBuffer(map);
            channel.close();
            accessFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 分片记录入库
        saveSysChunkRecord(file, param);

        // 检测是否为最后一块分片
        QueryWrapper<SysChunkRecord> wrapper1 = new QueryWrapper<SysChunkRecord>()
                .eq("file_md5", param.getMd5());
        Long count = sysChunkRecordMapper.selectCount(wrapper1);


        Integer temp = new Integer(count.intValue());

        if (temp.equals(param.getTotalChunk())) {
            try {
                // 文件md5检验
                checkMd5(new FileInputStream(file), param.getMd5());
                // 文件上传记录入库
                String kid = saveSysFile(file, param);
                return vo.setUploaded(true).setFileKid(kid);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                // 清除分片记录
                cleanChunkData(param.getMd5());
            }
        }
        return vo.setUploaded(false);
    }

    /**
     * 普通上传
     */
    public UploadResultVo singleUpload(MultipartFileParam param) {
        UploadResultVo vo = new UploadResultVo();

        // 因为表设置了唯一约束，在插入数据前先判断数据是否已经存在
        SysFile sf = queryByMd5(param.getMd5());
        if (ObjectUtil.isNotEmpty(sf)) {
            return vo.setUploaded(true).setFileKid(sf.getKid());
        }

        File file = buildUploadFile(param);
        MultipartFile multipartFile = param.getFile();
        try {
            // 文件md5校验
            checkMd5(multipartFile.getInputStream(), param.getMd5());
            // 写文件
            multipartFile.transferTo(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 文件上传记录入库
        String kid = saveSysFile(file, param);
        return vo.setUploaded(true).setFileKid(kid);
    }



    /**
     * 秒传检测
     */
    public UploadResultVo fastUploadCheck(Boolean isChunk, String md5) {
        UploadResultVo vo = new UploadResultVo();

        // 文件表查找
        QueryWrapper<SysFile> wrapper = new QueryWrapper<SysFile>()
                .eq("file_md5", md5);
        List<SysFile> sysFileList = sysFileMapper.selectList(wrapper);
        if (sysFileList.size() > 0) {
            String kid = sysFileList.get(0).getKid();
            vo.setUploaded(true).setFileKid(kid);
        }
        if (!vo.getUploaded() && isChunk) {
            // 分片记录表查询
            QueryWrapper<SysChunkRecord> chunkWrapper = new QueryWrapper<SysChunkRecord>()
                    .eq("file_md5", md5);
            List<SysChunkRecord> sysChunkRecordList = sysChunkRecordMapper.selectList(chunkWrapper);
            List<Integer> chunkNum = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(sysChunkRecordList)) {
                 chunkNum = sysChunkRecordList.stream().map(SysChunkRecord::getChunk).collect(Collectors.toList());
            }
            vo.setChunkNum(chunkNum);
        }
        return vo;
    }


    /**
     * md5校验
     */
    private void checkMd5(InputStream is, String md5) {
        String check = "";
        try {
            check = DigestUtils.md5DigestAsHex(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!check.equalsIgnoreCase(md5)) {
            throw new CustomException(MsgConstant.MD5_CHECK_EXCEPTION);
        }
    }

    /**
     * 根据md5值清除分片数据
     */
    private void cleanChunkData(String md5) {
        QueryWrapper<SysChunkRecord> wrapper = new QueryWrapper<SysChunkRecord>()
                .eq("file_md5", md5);
        sysChunkRecordMapper.delete(wrapper);
    }

    /**
     * 文件上传记录入库
     */
    private String saveSysFile(File file, MultipartFileParam param) {
        QueryWrapper<SysFile> wrapper = new QueryWrapper<SysFile>()
                .eq("file_md5", param.getMd5());
        SysFile past = sysFileMapper.selectOne(wrapper);
        if (ObjectUtil.isEmpty(past)) {
            SysFile sysFile = new SysFile()
                    .setFileName(file.getName())
                    .setExtension(FileNameUtil.extName(file.getName()))
                    .setFileSize(param.getTotalSize())
                    .setFilePath(file.getAbsolutePath())
                    .setFileMd5(param.getMd5());
            sysFileMapper.insert(sysFile);
            return sysFile.getKid();
        }
        return past.getKid();
    }

    /**
     * 分片记录入库
     */
    private String saveSysChunkRecord(File file, MultipartFileParam param) {
        SysChunkRecord sysChunkRecord = new SysChunkRecord()
                .setChunkFileName(file.getName())
                .setChunkFilePath(file.getAbsolutePath())
                .setFileMd5(param.getMd5())
                .setChunkFileMd5(param.getChunkMd5())
                .setChunk(param.getChunk())
                .setTotalChunk(param.getTotalChunk());
        sysChunkRecordMapper.insert(sysChunkRecord);
        return sysChunkRecord.getKid();
    }

    /**
     * 查询md5在SysFile表中是否已存在
     */
    private SysFile queryByMd5(String md5) {
        QueryWrapper<SysFile> wrapper = new QueryWrapper<SysFile>()
                .eq("file_md5", md5);
        return sysFileMapper.selectOne(wrapper);
    }

    /**
     * 查询md5在SysChunkRecode表中是否已存在
     */
    private SysChunkRecord queryByChunkMd5(String chunkMd5) {
        QueryWrapper<SysChunkRecord> wrapper = new QueryWrapper<SysChunkRecord>()
                .eq("file_md5", chunkMd5);
        return sysChunkRecordMapper.selectOne(wrapper);
    }

    /**
     * 构建上传目录和文件
     */
    private File buildUploadFile(MultipartFileParam param) {
        String fileName = param.getFileName();
        String fullDir = buildUploadDir();
        return new File(fullDir, fileName);
    }

    /**
     * 构建上传完整目录
     */
    private String buildUploadDir() {
        String baseDir = fileConfig.getUploadDir();
        String dateDir = DateUtil.format(LocalDateTime.now(), "yyyy" + File.separator + "MM" + File.separator + "dd");
        String fullDir = baseDir + File.separator + dateDir;
        File dir = new File(fullDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return fullDir;
    }


    private void cleanBuffer(MappedByteBuffer map) {
        try {
            Method getCleanerMethod = map.getClass().getMethod("cleaner");
            Cleaner.create(map, null);
            getCleanerMethod.setAccessible(true);
            Cleaner cleaner = (Cleaner) getCleanerMethod.invoke(map);
            cleaner.clean();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    public String getFilePath(String kid) {
        SysFile sysFile = sysFileMapper.selectById(kid);
        return  sysFile.getFilePath();
    }
}
