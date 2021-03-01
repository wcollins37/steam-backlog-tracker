package com.tp.backlogtracker.persistence;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

class DoubleMapper implements RowMapper<Double> {

    String column = "";

    public DoubleMapper(String column) {
        this.column = column;
    }

    @Override
    public Double mapRow(ResultSet resultSet, int i) throws SQLException {
        return resultSet.getDouble(column);
    }
}
