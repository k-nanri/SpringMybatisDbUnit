package mng.doc.mapper;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.experimental.runners.Enclosed;

import javax.sql.DataSource;

import mng.doc.dao.Document;

import org.dbunit.Assertion;
import org.dbunit.DataSourceBasedDBTestCase;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.excel.XlsDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(Enclosed.class)
public class DocumentMapperTest {

    @RunWith(SpringJUnit4ClassRunner.class)
    @ContextConfiguration("classpath:applicationContext.xml")
    @Transactional
    public static class DocumentMapperTestUseDbUnit extends DataSourceBasedDBTestCase {

        Long nowTime = System.currentTimeMillis();

        @Autowired
        DocumentMapper mapper;

        @Autowired
        DataSource dataSourceTest;

        @Rule
        public TestName name = new TestName();


        @Test
        public void testSelectAll() throws Exception {
            prepare();
            List<Document> documents = this.mapper.selectAll();
        }

        @Test
        public void testInsertDocument() throws Exception {

            Document doc = new Document();
            doc.setName("C Program");
            doc.setRegistrant("ishi");
            doc.setRegistrationtime(new Date(nowTime));

            resetSequenceName("document_id_seq");
            Integer insertRows = this.mapper.insertDocument(doc);
            System.out.println("insertRows : " + insertRows);

            DatabaseConnection connection = new DatabaseConnection(this.dataSourceTest.getConnection());
            ITable actualTable = connection.createDataSet().getTable("document");

            String xlsname = name.getMethodName() + "_Expect.xls";
            InputStream is = getClass().getClassLoader().getResourceAsStream(xlsname);
            XlsDataSet dataSet = new XlsDataSet(is);
            ReplacementDataSet replacement = new ReplacementDataSet(dataSet);
            // [null]��null�ɒu��
            replacement.addReplacementObject("[null]", null);
            // [systemtime]�����ݓ����ɒu��
            TimeZone t = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("GMT"));

            replacement.addReplacementObject("[systemtime]", toDateTimeStr(nowTime));

            ITable expectedTable = replacement.getTable("document");
            Assertion.assertEquals(expectedTable, actualTable);
        }

        @Before
        public void setUp() throws Exception {
            // super.setUp()をコールすればテスト実行前にDBをsetup可能.
            // setUp()でコールしなければテストメソッド内でsetupが必要になる
            // super.setUp();
        }

        public void prepare() throws Exception {
            DatabaseConnection connection = new DatabaseConnection(this.dataSourceTest.getConnection());
            DatabaseOperation.CLEAN_INSERT.execute(connection, getDataSet());
        }

        public void resetSequenceName(String sequenceName) throws Exception {
            DatabaseConnection connection = new DatabaseConnection(this.dataSourceTest.getConnection());
            connection
                .getConnection()
                .createStatement()
                .executeQuery("select setval('" + sequenceName + "', 1, false)");
        }

        @Override
        protected DataSource getDataSource() {
            return this.dataSourceTest;
        }

        @Override
        protected IDataSet getDataSet() throws Exception {

            String pathName = name.getMethodName();

            String xlsname = pathName + ".xls";
            InputStream is = getClass().getClassLoader().getResourceAsStream(xlsname);
            XlsDataSet dataSet = new XlsDataSet(is);

            ReplacementDataSet replacement = new ReplacementDataSet(dataSet);
            // [null]��null�ɒu��
            replacement.addReplacementObject("[null]", null);
            // [systemtime]�����ݓ����ɒu��
            replacement.addReplacementObject("[systemtime]", toDateTimeStr(nowTime));

            return replacement;
        }

        private String toDateTimeStr(Long time) {
            return toDateTimeStr(new Date(time));
        }
        private String toDateTimeStr(Date date) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            String result = sdf.format(date);
            System.out.println(result);
            return result;
        }
    }

    @RunWith(SpringJUnit4ClassRunner.class)
    @ContextConfiguration("classpath:applicationContext.xml")
    @Transactional
    public static class DocumentMapperTestNotUseDbUnit {

        @Autowired
        DocumentMapper mapper;

        @Rule
        public TestName name = new TestName();


        @Test
        public void testSelectAll() {

            List<Document> documents = this.mapper.selectAll();
            for (Document doc : documents) {
                System.out.println(doc.toString());
            }
        }
    }
}