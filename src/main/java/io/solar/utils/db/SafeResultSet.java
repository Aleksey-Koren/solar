package io.solar.utils.db;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Map;

public class SafeResultSet implements ResultSet {
    private final ResultSet resultSet;

    public SafeResultSet(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    private void log(Exception e) {
        throw new RuntimeException(e);
    }

    @Override
    public boolean next() {
        try {
            return resultSet.next();
        } catch  (SQLException e) {
            log(e);
            return false;
        }
    }

    @Override
    public void close() {
        try {
            resultSet.close();
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public boolean wasNull() {
        try {
            return resultSet.wasNull();
        } catch  (SQLException e) {
            log(e);
            return false;
        }
    }

    @Override
    public String getString(int i) {
        try {
            return resultSet.getString(i);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public boolean getBoolean(int i) {
        try {
            return resultSet.getBoolean(i);
        } catch  (SQLException e) {
            log(e);
            return false;
        }
    }

    @Override
    public byte getByte(int i) {
        try {
            return resultSet.getByte(i);
        } catch  (SQLException e) {
            log(e);
            return 0;
        }
    }

    @Override
    public short getShort(int i) {
        try {
            return resultSet.getShort(i);
        } catch  (SQLException e) {
            log(e);
            return 0;
        }
    }

    @Override
    public int getInt(int i) {
        try {
            return resultSet.getInt(i);
        } catch  (SQLException e) {
            log(e);
            return 0;
        }
    }

    @Override
    public long getLong(int i) {
        try {
            return resultSet.getLong(i);
        } catch  (SQLException e) {
            log(e);
            return 0;
        }
    }

    @Override
    public float getFloat(int i) {
        try {
            return resultSet.getFloat(i);
        } catch  (SQLException e) {
            log(e);
            return 0;
        }
    }

    public Float fetchFloat(int i) {
        try {
            return resultSet.getObject(i, Float.class);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }
    public Float fetchFloat(String i) {
        try {
            return resultSet.getObject(i, Float.class);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public double getDouble(int i) {
        try {
            return resultSet.getDouble(i);
        } catch  (SQLException e) {
            log(e);
            return 0;
        }
    }

    @Override
    public BigDecimal getBigDecimal(int i, int i1) {
        try {
            return resultSet.getBigDecimal(i, i1);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public byte[] getBytes(int i) {
        try {
            return resultSet.getBytes(i);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public Date getDate(int i) {
        try {
            return resultSet.getDate(i);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public Time getTime(int i) {
        try {
            return resultSet.getTime(i);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public Timestamp getTimestamp(int i) {
        try {
            return resultSet.getTimestamp(i);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public InputStream getAsciiStream(int i) {
        try {
            return resultSet.getAsciiStream(i);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public InputStream getUnicodeStream(int i) {
        try {
            return resultSet.getUnicodeStream(i);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public InputStream getBinaryStream(int i) {
        try {
            return resultSet.getBinaryStream(i);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public String getString(String s) {
        try {
            return resultSet.getString(s);
        } catch  (SQLException e) {
            log(e);
            return "";
        }
    }

    @Override
    public boolean getBoolean(String s) {
        try {
            return resultSet.getBoolean(s);
        } catch  (SQLException e) {
            log(e);
            return false;
        }
    }

    @Override
    public byte getByte(String s) {
        try {
            return resultSet.getByte(s);
        } catch  (SQLException e) {
            log(e);
            return 0;
        }
    }

    @Override
    public short getShort(String s) {
        try {
            return resultSet.getShort(s);
        } catch  (SQLException e) {
            log(e);
            return 0;
        }
    }

    @Override
    public int getInt(String s) {
        try {
            return resultSet.getInt(s);
        } catch  (SQLException e) {
            log(e);
            return 0;
        }
    }

    @Override
    public long getLong(String s) {
        try {
            return resultSet.getLong(s);
        } catch  (SQLException e) {
            log(e);
            return 0;
        }
    }

    public Long fetchLong(String s) {
        try {
            return resultSet.getObject(s, Long.class);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    public Long fetchLong(int s) {
        try {
            return resultSet.getObject(s, Long.class);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public float getFloat(String s) {
        try {
            return resultSet.getFloat(s);
        } catch  (SQLException e) {
            log(e);
            return 0;
        }
    }

    @Override
    public double getDouble(String s) {
        try {
            return resultSet.getDouble(s);
        } catch  (SQLException e) {
            log(e);
            return 0;
        }
    }

    @Override
    public BigDecimal getBigDecimal(String s, int i) {
        try {
            return resultSet.getBigDecimal(s, i);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public byte[] getBytes(String s) {
        try {
            return resultSet.getBytes(s);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public Date getDate(String s) {
        try {
            return resultSet.getDate(s);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public Time getTime(String s) {
        try {
            return resultSet.getTime(s);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public Timestamp getTimestamp(String s) {
        try {
            return resultSet.getTimestamp(s);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public InputStream getAsciiStream(String s) {
        try {
            return resultSet.getAsciiStream(s);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public InputStream getUnicodeStream(String s) {
        try {
            return resultSet.getUnicodeStream(s);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public InputStream getBinaryStream(String s) {
        try {
            return resultSet.getBinaryStream(s);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public SQLWarning getWarnings() {
        try {
            return resultSet.getWarnings();
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public void clearWarnings() {
        try {
            resultSet.clearWarnings();
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public String getCursorName() {
        try {
            return resultSet.getCursorName();
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public ResultSetMetaData getMetaData() {
        try {
            return resultSet.getMetaData();
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public Object getObject(int i) {
        try {
            return resultSet.getObject(i);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public Object getObject(String s) {
        try {
            return resultSet.getObject(s);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public int findColumn(String s) {
        try {
            return resultSet.findColumn(s);
        } catch  (SQLException e) {
            log(e);
            return -1;
        }
    }

    @Override
    public Reader getCharacterStream(int i) {
        try {
            return resultSet.getCharacterStream(i);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public Reader getCharacterStream(String s) {
        try {
            return resultSet.getCharacterStream(s);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public BigDecimal getBigDecimal(int i) {
        try {
            return resultSet.getBigDecimal(i);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public BigDecimal getBigDecimal(String s) {
        try {
            return resultSet.getBigDecimal(s);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public boolean isBeforeFirst() {
        try {
            return resultSet.isBeforeFirst();
        } catch  (SQLException e) {
            log(e);
            return false;
        }
    }

    @Override
    public boolean isAfterLast() {
        try {
            return resultSet.isAfterLast();
        } catch  (SQLException e) {
            log(e);
            return false;
        }
    }

    @Override
    public boolean isFirst() {
        try {
            return resultSet.isFirst();
        } catch  (SQLException e) {
            log(e);
            return false;
        }
    }

    @Override
    public boolean isLast() {
        try {
            return resultSet.isLast();
        } catch  (SQLException e) {
            log(e);
            return false;
        }
    }

    @Override
    public void beforeFirst() {
        try {
            resultSet.beforeFirst();
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void afterLast() {
        try {
            resultSet.afterLast();
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public boolean first() {
        try {
            return resultSet.first();
        } catch  (SQLException e) {
            log(e);
            return false;
        }
    }

    @Override
    public boolean last() {
        try {
            return resultSet.last();
        } catch  (SQLException e) {
            log(e);
            return false;
        }
    }

    @Override
    public int getRow() {
        try {
            return resultSet.getRow();
        } catch  (SQLException e) {
            log(e);
            return -1;
        }
    }

    @Override
    public boolean absolute(int i) {
        try {
            return resultSet.absolute(i);
        } catch  (SQLException e) {
            log(e);
            return false;
        }
    }

    @Override
    public boolean relative(int i) {
        try {
            return resultSet.relative(i);
        } catch  (SQLException e) {
            log(e);
            return false;
        }
    }

    @Override
    public boolean previous() {
        try {
            return resultSet.previous();
        } catch  (SQLException e) {
            log(e);
            return false;
        }
    }

    @Override
    public void setFetchDirection(int i) {
        try {
            resultSet.setFetchDirection(i);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public int getFetchDirection() {
        try {
            return resultSet.getFetchDirection();
        } catch  (SQLException e) {
            log(e);
            return ResultSet.FETCH_FORWARD;
        }
    }

    @Override
    public void setFetchSize(int i) {
        try {
            resultSet.setFetchSize(i);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public int getFetchSize() {
        try {
            return resultSet.getFetchSize();
        } catch  (SQLException e) {
            log(e);
            return -1;
        }
    }

    @Override
    public int getType() {
        try {
            return resultSet.getType();
        } catch  (SQLException e) {
            log(e);
            return ResultSet.TYPE_FORWARD_ONLY;
        }
    }

    @Override
    public int getConcurrency() {
        try {
            return resultSet.getConcurrency();
        } catch  (SQLException e) {
            log(e);
            return ResultSet.CONCUR_READ_ONLY;
        }
    }

    @Override
    public boolean rowUpdated() {
        try {
            return resultSet.rowUpdated();
        } catch  (SQLException e) {
            log(e);
            return false;
        }
    }

    @Override
    public boolean rowInserted() {
        try {
            return resultSet.rowInserted();
        } catch  (SQLException e) {
            log(e);
            return false;
        }
    }

    @Override
    public boolean rowDeleted() {
        try {
            return resultSet.rowDeleted();
        } catch  (SQLException e) {
            log(e);
            return false;
        }
    }

    @Override
    public void updateNull(int i) {
        try {
            resultSet.updateNull(i);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateBoolean(int i, boolean b) {
        try {
            resultSet.updateBoolean(i, b);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateByte(int i, byte b) {
        try {
            resultSet.updateByte(i, b);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateShort(int i, short i1) {
        try {
            resultSet.updateShort(i, i1);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateInt(int i, int i1) {
        try {
            resultSet.updateInt(i, i1);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateLong(int i, long l) {
        try {
            resultSet.updateLong(i, l);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateFloat(int i, float v) {
        try {
            resultSet.updateFloat(i, v);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateDouble(int i, double v) {
        try {
            resultSet.updateDouble(i, v);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateBigDecimal(int i, BigDecimal bigDecimal) {
        try {
            resultSet.updateBigDecimal(i, bigDecimal);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateString(int i, String s) {
        try {
            resultSet.updateString(i, s);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateBytes(int i, byte[] bytes) {
        try {
            resultSet.updateBytes(i, bytes);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateDate(int i, Date date) {
        try {
            resultSet.updateDate(i, date);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateTime(int i, Time time) {
        try {
            resultSet.updateTime(i, time);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateTimestamp(int i, Timestamp timestamp) {
        try {
            resultSet.updateTimestamp(i, timestamp);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateAsciiStream(int i, InputStream inputStream, int i1) {
        try {
            resultSet.updateAsciiStream(i, inputStream, i1);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateBinaryStream(int i, InputStream inputStream, int i1) {
        try {
            resultSet.updateBinaryStream(i, inputStream, i1);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateCharacterStream(int i, Reader reader, int i1) {
        try {
            resultSet.updateCharacterStream(i, reader, i1);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateObject(int i, Object o, int i1) {
        try {
            resultSet.updateObject(i, o, i1);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateObject(int i, Object o) {
        try {
            resultSet.updateObject(i, o);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateNull(String s) {
        try {
            resultSet.updateNull(s);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateBoolean(String s, boolean b) {
        try {
            resultSet.updateBoolean(s, b);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateByte(String s, byte b) {
        try {
            resultSet.updateByte(s, b);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateShort(String s, short i) {
        try {
            resultSet.updateShort(s, i);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateInt(String s, int i) {
        try {
            resultSet.updateInt(s, i);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateLong(String s, long l) {
        try {
            resultSet.updateLong(s, l);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateFloat(String s, float v) {
        try {
            resultSet.updateFloat(s, v);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateDouble(String s, double v) {
        try {
            resultSet.updateDouble(s, v);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateBigDecimal(String s, BigDecimal bigDecimal) {
        try {
            resultSet.updateBigDecimal(s, bigDecimal);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateString(String s, String s1) {
        try {
            resultSet.updateString(s, s1);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateBytes(String s, byte[] bytes) {
        try {
            resultSet.updateBytes(s, bytes);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateDate(String s, Date date) {
        try {
            resultSet.updateDate(s, date);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateTime(String s, Time time) {
        try {
            resultSet.updateTime(s, time);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateTimestamp(String s, Timestamp timestamp) {
        try {
            resultSet.updateTimestamp(s, timestamp);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateAsciiStream(String s, InputStream inputStream, int i) {
        try {
            resultSet.updateAsciiStream(s, inputStream, i);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateBinaryStream(String s, InputStream inputStream, int i) {
        try {
            resultSet.updateBinaryStream(s, inputStream, i);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateCharacterStream(String s, Reader reader, int i) {
        try {
            resultSet.updateCharacterStream(s, reader, i);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateObject(String s, Object o, int i) {
        try {
            resultSet.updateObject(s, o, i);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateObject(String s, Object o) {
        try {
            resultSet.updateObject(s, o);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void insertRow() {
        try {
            resultSet.insertRow();
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateRow() {
        try {
            resultSet.updateRow();
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void deleteRow() {
        try {
            resultSet.deleteRow();
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void refreshRow() {
        try {
            resultSet.refreshRow();
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void cancelRowUpdates() {
        try {
            resultSet.cancelRowUpdates();
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void moveToInsertRow() {
        try {
            resultSet.moveToInsertRow();
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void moveToCurrentRow() {
        try {
            resultSet.moveToCurrentRow();
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public Statement getStatement() {
        try {
            return resultSet.getStatement();
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public Object getObject(int i, Map<String, Class<?>> map) {
        try {
            return resultSet.getObject(i, map);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public Ref getRef(int i) {
        try {
            return resultSet.getRef(i);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public Blob getBlob(int i) {
        try {
            return resultSet.getBlob(i);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public Clob getClob(int i) {
        try {
            return resultSet.getClob(i);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public Array getArray(int i) {
        try {
            return resultSet.getArray(i);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public Object getObject(String s, Map<String, Class<?>> map) {
        try {
            return resultSet.getObject(s, map);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public Ref getRef(String s) {
        try {
            return resultSet.getRef(s);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public Blob getBlob(String s) {
        try {
            return resultSet.getBlob(s);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public Clob getClob(String s) {
        try {
            return resultSet.getClob(s);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public Array getArray(String s) {
        try {
            return resultSet.getArray(s);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public Date getDate(int i, Calendar calendar) {
        try {
            return resultSet.getDate(i, calendar);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public Date getDate(String s, Calendar calendar) {
        try {
            return resultSet.getDate(s, calendar);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public Time getTime(int i, Calendar calendar) {
        try {
            return resultSet.getTime(i, calendar);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public Time getTime(String s, Calendar calendar) {
        try {
            return resultSet.getTime(s, calendar);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public Timestamp getTimestamp(int i, Calendar calendar) {
        try {
            return resultSet.getTimestamp(i, calendar);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public Timestamp getTimestamp(String s, Calendar calendar) {
        try {
            return resultSet.getTimestamp(s, calendar);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public URL getURL(int i) {
        try {
            return resultSet.getURL(i);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public URL getURL(String s) {
        try {
            return resultSet.getURL(s);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public void updateRef(int i, Ref ref) {
        try {
            resultSet.updateRef(i, ref);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateRef(String s, Ref ref) {
        try {
            resultSet.updateRef(s, ref);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateBlob(int i, Blob blob) {
        try {
            resultSet.updateBlob(i, blob);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateBlob(String s, Blob blob) {
        try {
            resultSet.updateBlob(s, blob);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateClob(int i, Clob clob) {
        try {
            resultSet.updateClob(i, clob);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateClob(String s, Clob clob) {
        try {
            resultSet.updateClob(s, clob);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateArray(int i, Array array) {
        try {
            resultSet.updateArray(i, array);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateArray(String s, Array array) {
        try {
            resultSet.updateArray(s, array);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public RowId getRowId(int i) {
        try {
            return resultSet.getRowId(i);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public RowId getRowId(String s) {
        try {
            return resultSet.getRowId(s);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public void updateRowId(int i, RowId rowId) {
        try {
            resultSet.updateRowId(i, rowId);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateRowId(String s, RowId rowId) {
        try {
            resultSet.updateRowId(s, rowId);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public int getHoldability() {
        try {
            return resultSet.getHoldability();
        } catch  (SQLException e) {
            log(e);
            return ResultSet.HOLD_CURSORS_OVER_COMMIT;
        }
    }

    @Override
    public boolean isClosed() {
        try {
            return resultSet.isClosed();
        } catch  (SQLException e) {
            log(e);
            return false;
        }
    }

    @Override
    public void updateNString(int i, String s) {
        try {
            resultSet.updateNString(i, s);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateNString(String s, String s1) {
        try {
            resultSet.updateNString(s, s1);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateNClob(int i, NClob nClob) {
        try {
            resultSet.updateNClob(i, nClob);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateNClob(String s, NClob nClob) {
        try {
            resultSet.updateNClob(s, nClob);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public NClob getNClob(int i) {
        try {
            return resultSet.getNClob(i);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public NClob getNClob(String s) {
        try {
            return resultSet.getNClob(s);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public SQLXML getSQLXML(int i) {
        try {
            return resultSet.getSQLXML(i);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public SQLXML getSQLXML(String s) {
        try {
            return resultSet.getSQLXML(s);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public void updateSQLXML(int i, SQLXML sqlxml) {
        try {
            resultSet.updateSQLXML(i, sqlxml);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateSQLXML(String s, SQLXML sqlxml) {
        try {
            resultSet.updateSQLXML(s, sqlxml);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public String getNString(int i) {
        try {
            return resultSet.getNString(i);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public String getNString(String s) {
        try {
            return resultSet.getNString(s);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public Reader getNCharacterStream(int i) {
        try {
            return resultSet.getNCharacterStream(i);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public Reader getNCharacterStream(String s) {
        try {
            return resultSet.getNCharacterStream(s);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public void updateNCharacterStream(int i, Reader reader, long l) {
        try {
            resultSet.updateNCharacterStream(i, reader, l);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateNCharacterStream(String s, Reader reader, long l) {
        try {
            resultSet.updateNCharacterStream(s, reader, l);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateAsciiStream(int i, InputStream inputStream, long l) {
        try {
            resultSet.updateAsciiStream(i, inputStream, l);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateBinaryStream(int i, InputStream inputStream, long l) {
        try {
            resultSet.updateBinaryStream(i, inputStream, l);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateCharacterStream(int i, Reader reader, long l) {
        try {
            resultSet.updateCharacterStream(i, reader, l);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateAsciiStream(String s, InputStream inputStream, long l) {
        try {
            resultSet.updateAsciiStream(s, inputStream, l);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateBinaryStream(String s, InputStream inputStream, long l) {
        try {
            resultSet.updateBinaryStream(s, inputStream, l);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateCharacterStream(String s, Reader reader, long l) {
        try {
            resultSet.updateCharacterStream(s, reader, l);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateBlob(int i, InputStream inputStream, long l) {
        try {
            resultSet.updateBlob(i, inputStream, l);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateBlob(String s, InputStream inputStream, long l) {
        try {
            resultSet.updateBlob(s, inputStream, l);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateClob(int i, Reader reader, long l) {
        try {
            resultSet.updateClob(i, reader, l);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateClob(String s, Reader reader, long l) {
        try {
            resultSet.updateClob(s, reader, l);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateNClob(int i, Reader reader, long l) {
        try {
            resultSet.updateNClob(i, reader, l);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateNClob(String s, Reader reader, long l) {
        try {
            resultSet.updateNClob(s, reader, l);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateNCharacterStream(int i, Reader reader) {
        try {
            resultSet.updateNCharacterStream(i, reader);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateNCharacterStream(String s, Reader reader) {
        try {
            resultSet.updateNCharacterStream(s, reader);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateAsciiStream(int i, InputStream inputStream) {
        try {
            resultSet.updateAsciiStream(i, inputStream);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateBinaryStream(int i, InputStream inputStream) {
        try {
            resultSet.updateBinaryStream(i, inputStream);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateCharacterStream(int i, Reader reader) {
        try {
            resultSet.updateCharacterStream(i, reader);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateAsciiStream(String s, InputStream inputStream) {
        try {
            resultSet.updateAsciiStream(s, inputStream);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateBinaryStream(String s, InputStream inputStream) {
        try {
            resultSet.updateBinaryStream(s, inputStream);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateCharacterStream(String s, Reader reader) {
        try {
            resultSet.updateCharacterStream(s, reader);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateBlob(int i, InputStream inputStream) {
        try {
            resultSet.updateBlob(i, inputStream);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateBlob(String s, InputStream inputStream) {
        try {
            resultSet.updateBlob(s, inputStream);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateClob(int i, Reader reader) {
        try {
            resultSet.updateClob(i, reader);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateClob(String s, Reader reader) {
        try {
            resultSet.updateClob(s, reader);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateNClob(int i, Reader reader) {
        try {
            resultSet.updateNClob(i, reader);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public void updateNClob(String s, Reader reader) {
        try {
            resultSet.updateNClob(s, reader);
        } catch  (SQLException e) {
            log(e);
        }
    }

    @Override
    public <T> T getObject(int i, Class<T> aClass) {
        try {
            return resultSet.getObject(i, aClass);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public <T> T getObject(String s, Class<T> aClass) {
        try {
            return resultSet.getObject(s, aClass);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public <T> T unwrap(Class<T> aClass) {
        try {
            return resultSet.unwrap(aClass);
        } catch  (SQLException e) {
            log(e);
            return null;
        }
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) {
        try {
            return resultSet.isWrapperFor(aClass);
        } catch  (SQLException e) {
            log(e);
            return false;
        }
    }
}
