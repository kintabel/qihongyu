package com.study.ssm.dao;

import com.study.ssm.bean.Fileses;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface FileDao {
    @Insert("insert into test03.file (name, uploader, size, count, type, path, file) values (#{name}, #{uploader}, #{size}, #{count}, #{type}, #{path}, #{file})")
    void insert(Fileses files);

    //  获取按分类的文件列表，由PageHelper处理分页
    @Select("SELECT * FROM test03.file WHERE type LIKE CONCAT('%', #{category}, '%')")
    List<Fileses> getFilesByCategory(@Param("category") String category);

    // 获取所有文件列表，由PageHelper处理分页
    @Select("SELECT * FROM test03.file ORDER BY id DESC")
    List<Fileses> getAllFiles();

    @Select("SELECT COUNT(*) FROM test03.file WHERE type LIKE CONCAT('%', #{category}, '%')")
    int getTotalCountByCategory(@Param("category") String category);

    @Select("SELECT COUNT(*) FROM test03.file")
    int getTotalFileCount();

    @Select("SELECT * FROM test03.file WHERE id = #{id}")
    Fileses getFileById(int id);

    @Update("UPDATE test03.file SET count = count + 1 WHERE id = #{id}")
    void addCount(Integer id);

    @Select("SELECT DISTINCT type FROM test03.file")
    List<String> getAllFileTypes();

    @Select("SELECT id, name, uploader, size, count, type, path, file FROM test03.file WHERE type LIKE CONCAT('%', #{category}, '%') LIMIT #{offset}, #{limit}")
    List<Fileses> searchFilesByCategory(@Param("category") String category, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT id, name, uploader, size, count, type, path, file FROM test03.file LIMIT #{offset}, #{limit}")
    List<Fileses> searchAllFiles(@Param("offset") int offset, @Param("limit") int limit);
}