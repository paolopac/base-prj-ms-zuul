package com.faraday.webapp.dto.response;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import lombok.Data;
import lombok.Setter;
import lombok.AccessLevel;

@Data
public class ErrorDTORes {

  @Setter(AccessLevel.NONE) 
  private String date;
  
  private int code;
  
  private String message;

  public void setDate(String date) {
    if(date != null && date.isEmpty()){
      this.date = date;
    } else {
      OffsetDateTime dateTime = OffsetDateTime.now();
      DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
      this.date = dateTime.format(customFormatter);
    } 
  }
  
}