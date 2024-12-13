package com.leebuntu.db.query;

import com.leebuntu.db.query.enums.QueryType;

import java.util.*;
import java.util.regex.*;

public class QueryParser {
    private QueryParser() {
    }

    private static class SingletonHelper {
        private static final QueryParser instance = new QueryParser();
    }

    public static QueryParser getInstance() {
        return SingletonHelper.instance;
    }

    private static final String SELECT_REGEX = "^SELECT\\s+(\\*|[\\w, ]+)\\s+FROM\\s+(\\w+)(?:\\s+WHERE\\s+(\\w+)\\s*=\\s*\\?)?$";
    private static final String INSERT_REGEX = "^INSERT\\s+INTO\\s+(\\w+)\\s+(.+)$";
    private static final String UPDATE_REGEX = "^UPDATE\\s+FROM\\s+(\\w+)\\s+([\\w\\s=,\\?]+)\\s+WHERE\\s+(\\w+)\\s*=\\s*\\?$";
    private static final String DELETE_REGEX = "^DELETE\\s+FROM\\s+(\\w+)\\s+WHERE\\s+(\\w+)\\s*=\\s*\\?$";

    private QueryType getQueryType(String syntax) {
        String queryTypeString = syntax.trim().substring(0, Math.min(6, syntax.trim().length())).toUpperCase();
        try {
            return QueryType.valueOf(queryTypeString);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid query type: " + queryTypeString);
        }
    }

    private Query createQuery(QueryType queryType, String tableName, List<String> targetColumnNames,
                              String whereColumnName, List<Object> parameters) {
        Query query = new Query();
        query.setQueryType(queryType);
        query.setTableName(tableName);
        query.setTargetColumnNames(targetColumnNames);
        query.setWhereColumnName(whereColumnName);
        query.setParameters(parameters);
        return query;
    }

    private Query parseSelectQuery(String syntax, List<Object> parameters) {
        Pattern selectPattern = Pattern.compile(SELECT_REGEX);
        Matcher selectMatcher = selectPattern.matcher(syntax);
        if (selectMatcher.matches()) {
            String columns = selectMatcher.group(1);
            List<String> targetColumnNames = columns.equals("*") ? List.of("*") : Arrays.asList(columns.split(",\\s*"));

            String whereColumnName = null;
            if (selectMatcher.group(3) != null) {
                whereColumnName = selectMatcher.group(3);
            } else {
                whereColumnName = null;
            }

            return createQuery(QueryType.SELECT, selectMatcher.group(2), targetColumnNames, whereColumnName,
                    parameters);
        } else {
            throw new IllegalArgumentException("Invalid query!");
        }
    }

    private Query parseInsertQuery(String syntax, List<Object> parameters) {
        Pattern insertPattern = Pattern.compile(INSERT_REGEX);
        Matcher insertMatcher = insertPattern.matcher(syntax);
        if (insertMatcher.matches()) {
            String[] assignments = insertMatcher.group(2).split(",\\s*");
            List<String> targetColumnNames = new ArrayList<>();
            for (String assignment : assignments) {
                String[] parts = assignment.split("=");
                targetColumnNames.add(parts[0].trim());
            }
            return createQuery(QueryType.INSERT, insertMatcher.group(1), targetColumnNames, null, parameters);
        } else {
            throw new IllegalArgumentException("Invalid query!");
        }
    }

    private Query parseUpdateQuery(String syntax, List<Object> parameters) {
        Pattern updatePattern = Pattern.compile(UPDATE_REGEX);
        Matcher updateMatcher = updatePattern.matcher(syntax);
        if (updateMatcher.matches()) {
            String[] assignments = updateMatcher.group(2).split(",\\s*");
            List<String> targetColumnNames = new ArrayList<>();
            for (String assignment : assignments) {
                String[] parts = assignment.split("=");
                targetColumnNames.add(parts[0].trim());
            }
            return createQuery(QueryType.UPDATE, updateMatcher.group(1), targetColumnNames, updateMatcher.group(3),
                    parameters);
        } else {
            throw new IllegalArgumentException("Invalid query!");
        }
    }

    private Query parseDeleteQuery(String syntax, List<Object> parameters) {
        Pattern deletePattern = Pattern.compile(DELETE_REGEX);
        Matcher deleteMatcher = deletePattern.matcher(syntax);
        if (deleteMatcher.matches()) {
            return createQuery(QueryType.DELETE, deleteMatcher.group(1), null, deleteMatcher.group(2), parameters);
        } else {
            throw new IllegalArgumentException("Invalid query!");
        }
    }

    public Query parse(String query, List<Object> parameters) {
        switch (getQueryType(query)) {
            case SELECT:
                return parseSelectQuery(query, parameters);
            case INSERT:
                return parseInsertQuery(query, parameters);
            case UPDATE:
                return parseUpdateQuery(query, parameters);
            case DELETE:
                return parseDeleteQuery(query, parameters);
            default:
                throw new IllegalArgumentException("Unsupported query type");
        }
    }
}