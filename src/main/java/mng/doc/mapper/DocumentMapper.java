package mng.doc.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import mng.doc.dao.Document;

public interface DocumentMapper {

    public List<Document> selectAll();
    public Integer insertDocument(Document doc);
    public Integer updateDocument(@Param("id") Integer id, @Param("editor") String editor, @Param("edittime") Date edittime);
    public Integer deleteDocument(Integer id);
}
