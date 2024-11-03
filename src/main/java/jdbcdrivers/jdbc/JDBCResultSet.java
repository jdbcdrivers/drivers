package jdbcdrivers.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import java.util.Objects;

import jdbcdrivers.generic.api.IGenericResultSet;
import jdbcdrivers.generic.exceptions.AlreadyClosedException;
import jdbcdrivers.generic.exceptions.GenericProtocolException;
import jdbcdrivers.generic.exceptions.ResultSetClosedException;
import jdbcdrivers.generic.exceptions.WrongColumnTypeException;

/**
 * JDBC {@link ResultSet} implementation, delegates to the generic driver result set implementation.
 */
final class JDBCResultSet extends JDBCResultEntity implements ResultSet {

    private final JDBCResultSetMetaData metaData;
    private final IGenericResultSet genericResultSet;

    JDBCResultSet(IGenericResultSet genericResultSet) {
        super(genericResultSet.getResultSetMetaData());

        this.metaData = new JDBCResultSetMetaData(genericResultSet.getResultSetMetaData());
        this.genericResultSet = Objects.requireNonNull(genericResultSet);
    }

    @Override
    public boolean next() throws SQLException {

        boolean hasNext;

        try {
            hasNext = genericResultSet.next();
        }
        catch (GenericProtocolException ex) {

            throw convert(ex);
        }

        return hasNext;
    }

    @Override
    public void close() throws SQLException {

        try {
            genericResultSet.close();
        }
        catch (AlreadyClosedException ex) {

            throw convert(ex);
        }
        catch (GenericProtocolException ex) {

            throw convert(ex);
        }
    }

    @Override
    public boolean wasNull() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public String getString(int columnIndex) throws SQLException {

        final String result;

        try {
            result = genericResultSet.getString(toIndex(columnIndex));
        }
        catch (ResultSetClosedException ex) {

            throw convert(ex);
        }
        catch (WrongColumnTypeException ex) {

            throw convert(ex);
        }

        return result;
    }

    @Override
    public boolean getBoolean(int columnIndex) throws SQLException {

        final boolean result;

        try {
            result = genericResultSet.getBoolean(toIndex(columnIndex));
        }
        catch (ResultSetClosedException ex) {

            throw convert(ex);
        }
        catch (WrongColumnTypeException ex) {

            throw convert(ex);
        }

        return result;
    }

    @Override
    public byte getByte(int columnIndex) throws SQLException {

        final byte result;

        try {
            result = genericResultSet.getByte(toIndex(columnIndex));
        }
        catch (ResultSetClosedException ex) {

            throw convert(ex);
        }
        catch (WrongColumnTypeException ex) {

            throw convert(ex);
        }

        return result;
    }

    @Override
    public short getShort(int columnIndex) throws SQLException {

        final short result;

        try {
            result = genericResultSet.getShort(toIndex(columnIndex));
        }
        catch (ResultSetClosedException ex) {

            throw convert(ex);
        }
        catch (WrongColumnTypeException ex) {

            throw convert(ex);
        }

        return result;
    }

    @Override
    public int getInt(int columnIndex) throws SQLException {

        final int result;

        try {
            result = genericResultSet.getInt(toIndex(columnIndex));
        }
        catch (ResultSetClosedException ex) {

            throw convert(ex);
        }
        catch (WrongColumnTypeException ex) {

            throw convert(ex);
        }

        return result;
    }

    @Override
    public long getLong(int columnIndex) throws SQLException {

        final long result;

        try {
            result = genericResultSet.getLong(toIndex(columnIndex));
        }
        catch (ResultSetClosedException ex) {

            throw convert(ex);
        }
        catch (WrongColumnTypeException ex) {

            throw convert(ex);
        }

        return result;
    }

    @Override
    public float getFloat(int columnIndex) throws SQLException {

        final float result;

        try {
            result = genericResultSet.getFloat(toIndex(columnIndex));
        }
        catch (ResultSetClosedException ex) {

            throw convert(ex);
        }
        catch (WrongColumnTypeException ex) {

            throw convert(ex);
        }

        return result;
    }

    @Override
    public double getDouble(int columnIndex) throws SQLException {

        final double result;

        try {
            result = genericResultSet.getDouble(toIndex(columnIndex));
        }
        catch (ResultSetClosedException ex) {

            throw convert(ex);
        }
        catch (WrongColumnTypeException ex) {

            throw convert(ex);
        }

        return result;
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {

        final BigDecimal result;

        try {
            result = genericResultSet.getBigDecimal(toIndex(columnIndex), scale);
        }
        catch (ResultSetClosedException ex) {

            throw convert(ex);
        }
        catch (WrongColumnTypeException ex) {

            throw convert(ex);
        }

        return result;
    }

    @Override
    public byte[] getBytes(int columnIndex) throws SQLException {

        throw new UnsupportedOperationException();
    }

    private static final int SECONDS_IN_MINUTE = 60;
    private static final int SECONDS_IN_HOUR = 60 * SECONDS_IN_MINUTE;
    private static final int SECONDS_IN_DAY = 24 * SECONDS_IN_HOUR;
    private static final int MILLIS_IN_DAY = 1000 * SECONDS_IN_HOUR;

    @Override
    public Date getDate(int columnIndex) throws SQLException {

        final int days;

        try {
            days = genericResultSet.getDate(toIndex(columnIndex));
        }
        catch (ResultSetClosedException ex) {

            throw convert(ex);
        }
        catch (WrongColumnTypeException ex) {

            throw convert(ex);
        }

        return new Date(days * MILLIS_IN_DAY);
    }

    @Override
    public Time getTime(int columnIndex) throws SQLException {

        final long time;

        try {
            time = genericResultSet.getTime(toIndex(columnIndex));
        }
        catch (ResultSetClosedException ex) {

            throw convert(ex);
        }
        catch (WrongColumnTypeException ex) {

            throw convert(ex);
        }

        final long millis;

        if (time > SECONDS_IN_DAY) {

            // millis since Epoch
            millis = time;
        }
        else {
            // seconds since start of day
            final long now = System.currentTimeMillis() ;
            final long startOfDay = now - (now % SECONDS_IN_DAY);

            final int seconds = (int)time;
/*
            final int hour = seconds / SECONDS_IN_HOUR;

            final int remainingMinutesInSeconds =  seconds - (hour * SECONDS_IN_HOUR);
*/
            millis = startOfDay + (seconds * 1000);
        }

        return new Time(millis);
    }

    @Override
    public Timestamp getTimestamp(int columnIndex) throws SQLException {

        final long millis;

        try {
            millis = genericResultSet.getTimestamp(toIndex(columnIndex));
        }
        catch (ResultSetClosedException ex) {

            throw convert(ex);
        }
        catch (WrongColumnTypeException ex) {

            throw convert(ex);
        }

        return new Timestamp(millis);
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {

        final Object result;

        try {
            result = genericResultSet.getObject(toIndex(columnIndex));
        }
        catch (ResultSetClosedException ex) {

            throw convert(ex);
        }
        catch (WrongColumnTypeException ex) {

            throw convert(ex);
        }

        return result;
    }

    @Override
    public InputStream getAsciiStream(int columnIndex) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream getBinaryStream(int columnIndex) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public String getString(String columnLabel) throws SQLException {

        Objects.requireNonNull(columnLabel);

        return getString(toColumnIndex(columnLabel));
    }

    @Override
    public boolean getBoolean(String columnLabel) throws SQLException {

        Objects.requireNonNull(columnLabel);

        return getBoolean(toColumnIndex(columnLabel));
    }

    @Override
    public byte getByte(String columnLabel) throws SQLException {

        Objects.requireNonNull(columnLabel);

        return getByte(toColumnIndex(columnLabel));
    }

    @Override
    public short getShort(String columnLabel) throws SQLException {

        Objects.requireNonNull(columnLabel);

        return getShort(toColumnIndex(columnLabel));
    }

    @Override
    public int getInt(String columnLabel) throws SQLException {

        Objects.requireNonNull(columnLabel);

        return getInt(toColumnIndex(columnLabel));
    }

    @Override
    public long getLong(String columnLabel) throws SQLException {

        Objects.requireNonNull(columnLabel);

        return getLong(toColumnIndex(columnLabel));
    }

    @Override
    public float getFloat(String columnLabel) throws SQLException {

        Objects.requireNonNull(columnLabel);

        return getFloat(toColumnIndex(columnLabel));
    }

    @Override
    public double getDouble(String columnLabel) throws SQLException {

        Objects.requireNonNull(columnLabel);

        return getDouble(toColumnIndex(columnLabel));
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {

        Objects.requireNonNull(columnLabel);

        return getBigDecimal(toColumnIndex(columnLabel));
    }

    @Override
    public byte[] getBytes(String columnLabel) throws SQLException {

        Objects.requireNonNull(columnLabel);

        return getBytes(toColumnIndex(columnLabel));
    }

    @Override
    public Date getDate(String columnLabel) throws SQLException {

        Objects.requireNonNull(columnLabel);

        return getDate(toColumnIndex(columnLabel));
    }

    @Override
    public Time getTime(String columnLabel) throws SQLException {

        Objects.requireNonNull(columnLabel);

        return getTime(toColumnIndex(columnLabel));
    }

    @Override
    public Timestamp getTimestamp(String columnLabel) throws SQLException {

        Objects.requireNonNull(columnLabel);

        return getTimestamp(toColumnIndex(columnLabel));
    }

    @Override
    public Object getObject(String columnLabel) throws SQLException {

        Objects.requireNonNull(columnLabel);

        return getObject(toColumnIndex(columnLabel));
    }

    @Override
    public InputStream getAsciiStream(String columnLabel) throws SQLException {

        Objects.requireNonNull(columnLabel);

        return getAsciiStream(toColumnIndex(columnLabel));
    }

    @Override
    public InputStream getUnicodeStream(String columnLabel) throws SQLException {

        Objects.requireNonNull(columnLabel);

        return getUnicodeStream(toColumnIndex(columnLabel));
    }

    @Override
    public InputStream getBinaryStream(String columnLabel) throws SQLException {

        Objects.requireNonNull(columnLabel);

        return getBinaryStream(toColumnIndex(columnLabel));
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void clearWarnings() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public String getCursorName() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {

        return metaData;
    }

    @Override
    public int findColumn(String columnLabel) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public Reader getCharacterStream(int columnIndex) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public Reader getCharacterStream(String columnLabel) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAfterLast() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isFirst() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isLast() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void beforeFirst() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void afterLast() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean first() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean last() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public int getRow() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean absolute(int row) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean relative(int rows) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean previous() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public int getFetchDirection() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public int getFetchSize() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public int getType() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public int getConcurrency() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean rowUpdated() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean rowInserted() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean rowDeleted() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateNull(int columnIndex) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateByte(int columnIndex, byte x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateShort(int columnIndex, short x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateInt(int columnIndex, int x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateLong(int columnIndex, long x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateFloat(int columnIndex, float x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateDouble(int columnIndex, double x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateString(int columnIndex, String x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateBytes(int columnIndex, byte[] x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateDate(int columnIndex, Date x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateTime(int columnIndex, Time x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateObject(int columnIndex, Object x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateNull(String columnLabel) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateBoolean(String columnLabel, boolean x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateByte(String columnLabel, byte x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateShort(String columnLabel, short x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateInt(String columnLabel, int x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateLong(String columnLabel, long x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateFloat(String columnLabel, float x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateDouble(String columnLabel, double x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateString(String columnLabel, String x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateBytes(String columnLabel, byte[] x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateDate(String columnLabel, Date x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateTime(String columnLabel, Time x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateObject(String columnLabel, Object x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void insertRow() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateRow() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteRow() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void refreshRow() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void cancelRowUpdates() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void moveToInsertRow() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void moveToCurrentRow() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public Statement getStatement() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public Ref getRef(int columnIndex) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public Blob getBlob(int columnIndex) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public Clob getClob(int columnIndex) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public Array getArray(int columnIndex) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public Ref getRef(String columnLabel) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public Blob getBlob(String columnLabel) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public Clob getClob(String columnLabel) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public Array getArray(String columnLabel) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public Date getDate(int columnIndex, Calendar cal) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public Date getDate(String columnLabel, Calendar cal) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public Time getTime(int columnIndex, Calendar cal) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public Time getTime(String columnLabel, Calendar cal) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public URL getURL(int columnIndex) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public URL getURL(String columnLabel) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateRef(int columnIndex, Ref x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateRef(String columnLabel, Ref x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateBlob(int columnIndex, Blob x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateBlob(String columnLabel, Blob x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateClob(int columnIndex, Clob x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateClob(String columnLabel, Clob x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateArray(int columnIndex, Array x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateArray(String columnLabel, Array x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public RowId getRowId(int columnIndex) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public RowId getRowId(String columnLabel) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateRowId(int columnIndex, RowId x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateRowId(String columnLabel, RowId x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public int getHoldability() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isClosed() throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateNString(int columnIndex, String nString) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateNString(String columnLabel, String nString) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public NClob getNClob(int columnIndex) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public NClob getNClob(String columnLabel) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public SQLXML getSQLXML(int columnIndex) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public SQLXML getSQLXML(String columnLabel) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public String getNString(int columnIndex) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public String getNString(String columnLabel) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public Reader getNCharacterStream(int columnIndex) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public Reader getNCharacterStream(String columnLabel) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateClob(int columnIndex, Reader reader) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateClob(String columnLabel, Reader reader) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {

        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {

        throw new UnsupportedOperationException();
    }
}
