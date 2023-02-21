package com.wittybrains.busbookingsystem.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.wittybrains.busbookingsystem.model.Station;
import com.wittybrains.busbookingsystem.model.TravelSchedule;

public class TravelScheduleDTO {
   
    private Long busId;
    private Station source;
    private Station destination;
    private Double fareAmount;
    private String estimatedArrivalTime;
    private String estimatedDepartureTime;
    private String date;
    
    
	public TravelScheduleDTO(TravelSchedule travelSchedule) {
		super();
		
		this.source=travelSchedule.getSource();
		this.destination=travelSchedule.getDestination();
		this.fareAmount=travelSchedule.getFareAmount();
		this.estimatedArrivalTime=travelSchedule.getEstimatedArrivalTime().toString();
		this.estimatedDepartureTime=travelSchedule.getEstimatedDepartureTime().toString();
	
	
	}
	public Long getBusId() {
		return busId;
	}
	public void setBusId(Long busId) {
		this.busId = busId;
	}
	public Station getSource() {
		return source;
	}
	public void setSource(Station source) {
		this.source = source;
	}
	public Station getDestination() {
		return destination;
	}
	public void setDestination(Station destination) {
		this.destination = destination;
	}
	public Double getFareAmount() {
		return fareAmount;
	}
	public void setFareAmount(Double fareAmount) {
		this.fareAmount = fareAmount;
	}
	public String getEstimatedArrivalTime() {
		return estimatedArrivalTime;
	}
	public void setEstimatedArrivalTime(String estimatedArrivalTime) {
		this.estimatedArrivalTime = estimatedArrivalTime;
	}
	public String getEstimatedDepartureTime() {
		return estimatedDepartureTime;
	}
	public void setEstimatedDepartureTime(String estimatedDepartureTime) {
		this.estimatedDepartureTime = estimatedDepartureTime;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
   

    

}
