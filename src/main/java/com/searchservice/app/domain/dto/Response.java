package com.searchservice.app.domain.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response extends BaseResponse{

	private List<String> data;
	private String token;

	private Integer dataSize;

	private List<TableListResponse> tableList;

	public Response(String token) {
		this.token = token;
	}

	public Response(int statusCode, String message) {
		super(statusCode, message);
	}

	public Response(int statusCode, String message, String token) {
		super(statusCode, message);
		this.token = token;
	}
	
	public Response(int statusCode, String message, int dataSize) {
		super(statusCode, message);
		this.dataSize = dataSize;
	}
	
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class TableListResponse {
		private Integer tenantId;
		private String tableName;
		
		public TableListResponse(String tableName) {
			this.tableName = tableName;
		}
	}

}
