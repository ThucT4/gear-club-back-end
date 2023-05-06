package com.pw.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HttpResponse {
    private String status;
    private String message;
}
