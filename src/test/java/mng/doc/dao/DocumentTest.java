package mng.doc.dao;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

public class DocumentTest {

    @Test
    public void testToString () {
        Document doc = new Document();
        doc.setEdittime(new Date(System.currentTimeMillis()));
        doc.setEditor("hoge");
        doc.setId(1);
        System.out.println(doc.toString());
    }

}
