package org.rainbow.security.service.reporter;

import java.util.Map;

public class ChangeReporterInfo {
	private String selectQuery;
	private Object[] selectArgs;
	private String sourceTableName;
	private String sourceIdColumnLabel;
	private String sourceIdColumnName;
	private String sourceLastUpdateDateColumnLabel;
	private String sourceLastUpdateDateColumnName;
	private String sourceUpdaterColumnLabel;
	private String sourceUpdaterColumnName;
	private String sourceVersionColumnLabel;
	private String sourceVersionColumnName;
	private Map<String, String> columnMaps;
	private String targetTableName;
	private String targetLastUpdateDateColumnName;
	private String targetUpdaterColumnName;
	private String targetVersionColumnName;
	private String targetWriteOperationColumnName;
	private WriteOperation targetWriteOperation;

	public ChangeReporterInfo() {
	}

	public ChangeReporterInfo(String selectQuery, Object[] selectArgs, String sourceTableName,
			String sourceIdColumnLabel, String sourceIdColumnName, String sourceLastUpdateDateColumnLabel,
			String sourceLastUpdateDateColumnName, String sourceUpdaterColumnLabel, String sourceUpdaterColumnName,
			String sourceVersionColumnLabel, String sourceVersionColumnName, Map<String, String> columnMaps,
			String targetTableName, String targetLastUpdateDateColumnName, String targetUpdaterColumnName,
			String targetVersionColumnName, String targetWriteOperationColumnName,
			WriteOperation targetWriteOperation) {

		this.selectQuery = selectQuery;
		this.selectArgs = selectArgs;
		this.sourceTableName = sourceTableName;
		this.sourceIdColumnLabel = sourceIdColumnLabel;
		this.sourceIdColumnName = sourceIdColumnName;
		this.sourceLastUpdateDateColumnLabel = sourceLastUpdateDateColumnLabel;
		this.sourceLastUpdateDateColumnName = sourceLastUpdateDateColumnName;
		this.sourceUpdaterColumnLabel = sourceUpdaterColumnLabel;
		this.sourceUpdaterColumnName = sourceUpdaterColumnName;
		this.sourceVersionColumnLabel = sourceVersionColumnLabel;
		this.sourceVersionColumnName = sourceVersionColumnName;
		this.columnMaps = columnMaps;
		this.targetTableName = targetTableName;
		this.targetLastUpdateDateColumnName = targetLastUpdateDateColumnName;
		this.targetUpdaterColumnName = targetUpdaterColumnName;
		this.targetVersionColumnName = targetVersionColumnName;
		this.targetWriteOperationColumnName = targetWriteOperationColumnName;
		this.targetWriteOperation = targetWriteOperation;
	}

	public String getSelectQuery() {
		return selectQuery;
	}

	public void setSelectQuery(String selectQuery) {
		this.selectQuery = selectQuery;
	}

	public Object[] getSelectArgs() {
		return selectArgs;
	}

	public void setSelectArgs(Object[] selectArgs) {
		this.selectArgs = selectArgs;
	}

	public String getSourceTableName() {
		return sourceTableName;
	}

	public void setSourceTableName(String sourceTableName) {
		this.sourceTableName = sourceTableName;
	}

	public String getSourceIdColumnLabel() {
		return sourceIdColumnLabel;
	}

	public void setSourceIdColumnLabel(String sourceIdColumnLabel) {
		this.sourceIdColumnLabel = sourceIdColumnLabel;
	}

	public String getSourceIdColumnName() {
		return sourceIdColumnName;
	}

	public void setSourceIdColumnName(String sourceIdColumnName) {
		this.sourceIdColumnName = sourceIdColumnName;
	}

	public String getSourceLastUpdateDateColumnLabel() {
		return sourceLastUpdateDateColumnLabel;
	}

	public void setSourceLastUpdateDateColumnLabel(String sourceLastUpdateDateColumnLabel) {
		this.sourceLastUpdateDateColumnLabel = sourceLastUpdateDateColumnLabel;
	}

	public String getSourceLastUpdateDateColumnName() {
		return sourceLastUpdateDateColumnName;
	}

	public void setSourceLastUpdateDateColumnName(String sourceLastUpdateDateColumnName) {
		this.sourceLastUpdateDateColumnName = sourceLastUpdateDateColumnName;
	}

	public String getSourceUpdaterColumnLabel() {
		return sourceUpdaterColumnLabel;
	}

	public void setSourceUpdaterColumnLabel(String sourceUpdaterColumnLabel) {
		this.sourceUpdaterColumnLabel = sourceUpdaterColumnLabel;
	}

	public String getSourceUpdaterColumnName() {
		return sourceUpdaterColumnName;
	}

	public void setSourceUpdaterColumnName(String sourceUpdaterColumnName) {
		this.sourceUpdaterColumnName = sourceUpdaterColumnName;
	}

	public String getSourceVersionColumnLabel() {
		return sourceVersionColumnLabel;
	}

	public void setSourceVersionColumnLabel(String sourceVersionColumnLabel) {
		this.sourceVersionColumnLabel = sourceVersionColumnLabel;
	}

	public String getSourceVersionColumnName() {
		return sourceVersionColumnName;
	}

	public void setSourceVersionColumnName(String sourceVersionColumnName) {
		this.sourceVersionColumnName = sourceVersionColumnName;
	}

	public Map<String, String> getColumnMaps() {
		return columnMaps;
	}

	public void setColumnMaps(Map<String, String> columnMaps) {
		this.columnMaps = columnMaps;
	}

	public String getTargetTableName() {
		return targetTableName;
	}

	public void setTargetTableName(String targetTableName) {
		this.targetTableName = targetTableName;
	}

	public String getTargetLastUpdateDateColumnName() {
		return targetLastUpdateDateColumnName;
	}

	public void setTargetLastUpdateDateColumnName(String targetLastUpdateDateColumnName) {
		this.targetLastUpdateDateColumnName = targetLastUpdateDateColumnName;
	}

	public String getTargetUpdaterColumnName() {
		return targetUpdaterColumnName;
	}

	public void setTargetUpdaterColumnName(String targetUpdaterColumnName) {
		this.targetUpdaterColumnName = targetUpdaterColumnName;
	}

	public String getTargetVersionColumnName() {
		return targetVersionColumnName;
	}

	public void setTargetVersionColumnName(String targetVersionColumnName) {
		this.targetVersionColumnName = targetVersionColumnName;
	}

	public String getTargetWriteOperationColumnName() {
		return targetWriteOperationColumnName;
	}

	public void setTargetWriteOperationColumnName(String targetWriteOperationColumnName) {
		this.targetWriteOperationColumnName = targetWriteOperationColumnName;
	}

	public WriteOperation getTargetWriteOperation() {
		return targetWriteOperation;
	}

	public void setTargetWriteOperation(WriteOperation tartgetWriteOperation) {
		this.targetWriteOperation = tartgetWriteOperation;
	}

}