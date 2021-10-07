package mng.doc.mapper;

import java.util.Date;
import java.util.List;
import org.junit.experimental.runners.Enclosed;
import mng.doc.dao.Document;
import mng.doc.test.util.MyTestExtendsDataSourceBasedDBTestCase;
import org.dbunit.Assertion;
import org.dbunit.dataset.ITable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(Enclosed.class)
public class DocumentMapperTest {

    @Sql("classpath:schema.sql")
    @RunWith(SpringJUnit4ClassRunner.class)
    @ContextConfiguration("classpath:applicationContext.xml")
    @Transactional
    public static class DocumentMapperTestUseDbUnit extends MyTestExtendsDataSourceBasedDBTestCase {

        @Autowired
        DocumentMapper mapper;

        @Test
        public void testSelectAll() throws Exception {

            // Setup database
            prepareTable(INSERT);
            List<Document> documents = this.mapper.selectAll();
        }

        @Test
        public void testInsertDocument() throws Exception {

            // Setup Insert data
            Document doc = new Document();
            doc.setName("C Program");
            doc.setRegistrant("ishi");
            doc.setRegistrationtime(new Date(nowTime));

            // Setup Database
            resetSequence("document_id_seq");

            Integer actual = this.mapper.insertDocument(doc);

            ITable actualTable = searchTable("document");
            ITable expectedTable = getExpectedTable("document");

            Assertion.assertEquals(expectedTable, actualTable);
            assertEquals(actual, new Integer(1));
        }

        @Test
        public void testUpdateDocument() throws Exception {

            Long updateTime = System.currentTimeMillis();
            prepareTable(INSERT);
            resetSequence("document_id_seq");
            addTag("[updateTime]", updateTime);

            Integer actual = this.mapper.updateDocument(1, "tanaka", new Date(updateTime));

            ITable actualTable = searchTable("document");
            ITable expectedTable = getExpectedTable("document");

            Assertion.assertEquals(expectedTable, actualTable);
            assertEquals(actual, new Integer(1));
        }

        @Test
        public void testDeleteDocument() throws Exception {

            prepareTable(INSERT);
            Integer actual = this.mapper.deleteDocument(1);

            ITable actualTable = searchTable("document");
            ITable expectedTable = getExpectedTable("document");

            Assertion.assertEquals(expectedTable, actualTable);
            assertEquals(actual, new Integer(1));
        }

        @Before
        public void setUp() throws Exception {
            // super.setUp()をコールすればテスト実行前にDBをsetup可能.
            // setUp()でコールしなければテストメソッド内でsetupが必要になる
            // super.setUp();
        }
    }
}