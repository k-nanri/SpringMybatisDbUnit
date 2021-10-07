package mng.doc.test.util;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;
import org.dbunit.DataSourceBasedDBTestCase;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.excel.XlsDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.springframework.beans.factory.annotation.Autowired;

public class MyTestExtendsDataSourceBasedDBTestCase extends
        DataSourceBasedDBTestCase {

    @Autowired
    protected DataSource dataSourceTest;

    @Rule
    public TestName testName = new TestName();

    /**
     * current time
     */
    protected Long nowTime = System.currentTimeMillis();

    /**
     * tag mapping
     */
    private Map<String, Object> tagMap = new HashMap<String, Object>();
    {
        this.tagMap.put("[null]", null);
        this.tagMap.put("[systemTime]", nowTime);
    }

    protected final String INSERT = "INSERT";
    protected final String DELETE_ALL = "DELETE_ALL";
    private Map<String, DatabaseOperation> databaseOperationes = new HashMap<String, DatabaseOperation>();
    {
        this.databaseOperationes.put(INSERT, DatabaseOperation.CLEAN_INSERT);
        this.databaseOperationes.put(DELETE_ALL, DatabaseOperation.DELETE_ALL);
    }

    /**
     * file extension
     */
    private String FILENAME_EXTENSION = ".xls";

    /**
     * add convert tag to value.
     * @param tag
     * @param value
     */
    public void addTag(String tag, Object value) {
        this.tagMap.put(tag, value);
    }

    /**
     * Return DataSource instance.
     */
    @Override
    protected DataSource getDataSource() {
        return dataSourceTest;
    }

    /**
     * Get DataSet from Excel file
     * @param filename
     * @return
     * @throws Exception
     */
    protected IDataSet getDataSet(String filename) throws Exception {
        InputStream is =
                getClass().getClassLoader().getResourceAsStream(filename);
        if (is == null) {
            throw new Exception("Cannot find " + filename);
        }
        XlsDataSet dataSet = new XlsDataSet(is);
        return dataSet;
    }

    /**
     * Return IDataSet instance.
     */
    @Override
    protected IDataSet getDataSet() throws Exception {

        String xlsName = testName.getMethodName() + FILENAME_EXTENSION;

        IDataSet dataSet = null;
        try {
            dataSet = getDataSet(xlsName);
        } catch (Exception ex) {
            xlsName = getClass().getSimpleName() + FILENAME_EXTENSION;
            dataSet = getDataSet(xlsName);
        }

        return replaceTag(dataSet);
     }

    /**
     * convert tag to values of map.
     * @param dataSet
     * @return
     */
    private IDataSet replaceTag(IDataSet dataSet) {

        ReplacementDataSet replacement = new ReplacementDataSet(dataSet);
        Set<String> keys = this.tagMap.keySet();
        for (String key : keys) {

            String tag = key;
            Object value = this.tagMap.get(key);
            if (tag.contains("Time")) {
                value = toDateTimeStr((Long) value);
            }
            replacement.addReplacementObject(key, value);
        }
        return replacement;
    }


    /**
     * convert long to string of yyyy-MM-dd HH:mm:ss.SSS format.
     * @param time date time of long
     * @return string of date time
     */
    protected String toDateTimeStr(Long time) {
        return toDateTimeStr(new Date(time));
    }

    /**
     * convert date instance to string of yyyy-MM-dd HH:mm:ss.SSS format.
     * @param date date time
     * @return string of date time
     */
    private String toDateTimeStr(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return sdf.format(date);
    }

    /**
     * get Table Data from Excel file of fileName.
     * @param fileName
     * @param tableName
     * @return
     * @throws Exception
     */
    protected ITable searchTable(String fileName, String tableName) throws Exception {
        IDataSet dataSet = getDataSet(fileName);
        dataSet = replaceTag(dataSet);
        return dataSet.getTable(tableName);
    }

    /**
     * get Table Data from table name.
     * @param tableName
     * @return
     * @throws Exception
     */
    protected ITable searchTable(String tableName) throws Exception {
        DatabaseConnection connection = getDBConnection();
        IDataSet dataSet = connection.createDataSet();
        dataSet = replaceTag(dataSet);

        ITable tableData = dataSet.getTable(tableName);

        return tableData;
    }

    /**
     * Get DatabaseConnection.
     * @return
     * @throws Exception
     */
    private DatabaseConnection getDBConnection() throws Exception {
        DatabaseConnection connection =
                new DatabaseConnection(this.dataSourceTest.getConnection());
        return connection;
    }

    /**
     * prepare table
     * @param filename
     * @param operation
     * @throws Exception
     */
    protected void prepareTable(String filename, String operation) throws Exception {
        DatabaseConnection connection = getDBConnection();
        DatabaseOperation databaseOperation = this.databaseOperationes.get(operation);

        IDataSet dataSet = null;
        if (filename == null) {
            dataSet = getDataSet();
        } else {
            dataSet = getDataSet(filename);
        }

        databaseOperation.execute(connection, dataSet);
    }

    /**
     * prepare table.
     * @param operation
     * @throws Exception
     */
    protected void prepareTable(String operation) throws Exception {
        prepareTable(null, operation);
    }

    /**
     * Set 1 to the sequence ID
     * @param sequenceName
     * @throws Exception
     */
    protected void resetSequence(String sequenceName) throws Exception {
        DatabaseConnection connection = getDBConnection();

        connection
            .getConnection()
            .createStatement()
            //.executeQuery("select setval('" + sequenceName + "', 1, false)");
            .execute("alter sequence " + sequenceName + " restart with 1");
    }

    /**
     * Get expected Table data from Excel file.
     * @param tablename
     * @return
     * @throws Exception
     */
    protected ITable getExpectedTable(String tablename) throws Exception {
        String xlsname = testName.getMethodName() + "_Expected" + FILENAME_EXTENSION;
        ITable expectedTable = searchTable(xlsname, tablename);
        return expectedTable;
    }
}
