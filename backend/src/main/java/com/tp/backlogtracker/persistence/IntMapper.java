package com.tp.backlogtracker.persistence;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

class IntMapper implements RowMapper<Integer> {

    String column = "";

    public IntMapper(String column) {
        this.column = column;
    }

    @Override
    public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
        return resultSet.getInt(column);
    }
}
