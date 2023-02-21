package com.wittybrains.busbookingsystem.service;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wittybrains.busbookingsystem.dto.TravelScheduleDTO;
import com.wittybrains.busbookingsystem.exception.InvalidSourceOrDestinationException;
import com.wittybrains.busbookingsystem.exception.StationNotFoundException;
import com.wittybrains.busbookingsystem.model.Bus;
import com.wittybrains.busbookingsystem.model.Station;
import com.wittybrains.busbookingsystem.model.TravelSchedule;
import com.wittybrains.busbookingsystem.repository.BusRepository;
import com.wittybrains.busbookingsystem.repository.StationRepository;
import com.wittybrains.busbookingsystem.repository.TravelScheduleRepository;

@Service
public class TravelScheduleService {
	private static final int MAX_SEARCH_DAYS = 30;
	@Autowired
	private TravelScheduleRepository scheduleRepository;

	@Autowired
	private BusRepository busRepository;
	
	@Autowired
	private StationRepository stationRepository;

	public Station getStationByCode(String code) {
	    Optional<Station> optionalStation = stationRepository.findByStationCode(code);
	    if (optionalStation.isPresent()) {
	        return optionalStation.get();
	    } else {
	        throw new StationNotFoundException("Station with code " + code + " not found");
	    }
	}



	public List<TravelScheduleDTO> getAvailableSchedules(Station source, Station destination, LocalDate searchDate) {
		LocalDateTime currentDateTime = LocalDateTime.now();
		LocalDate currentDate = currentDateTime.toLocalDate();
		LocalTime currentTime = currentDateTime.toLocalTime();

		LocalDateTime searchDateTime = LocalDateTime.of(searchDate, LocalTime.MIDNIGHT);
		if (searchDate.isBefore(currentDate)) {
			// cannot search for past schedules
			throw new IllegalArgumentException("Cannot search for schedules in the past");
		} else if (searchDate.equals(currentDate) ) {
			// search for schedules at least 1 hour from now
			searchDateTime = LocalDateTime.of(searchDate, currentTime.plusHours(1));
		}

		LocalDateTime maxSearchDateTime = currentDateTime.plusDays(MAX_SEARCH_DAYS);
		if (searchDateTime.isAfter(maxSearchDateTime)) {
			// cannot search for schedules more than one month in the future
			throw new IllegalArgumentException("Cannot search for schedules more than one month in the future");
		}

		List<TravelSchedule> travelScheduleList = scheduleRepository
				.findBySourceAndDestinationAndEstimatedArrivalTimeAfter(source, destination, currentDateTime);
		List<TravelScheduleDTO> travelScheduleDTOList = new ArrayList<>();
		for (TravelSchedule travelSchedule : travelScheduleList) {
			TravelScheduleDTO travelScheduleDTO = new TravelScheduleDTO(travelSchedule);
		
			travelScheduleDTOList.add(travelScheduleDTO);
		}
		return travelScheduleDTOList;
	}

	public boolean createSchedule(TravelScheduleDTO travelScheduleDTO) throws ParseException {
		if (travelScheduleDTO.getBusId() == null) {
			throw new IllegalArgumentException("Bus ID cannot be null");
		}

		Optional<Bus> optionalBus = busRepository.findById(travelScheduleDTO.getBusId());

		if (!optionalBus.isPresent()) {
			throw new IllegalArgumentException("Bus with ID " + travelScheduleDTO.getBusId() + " not found");
		}

		TravelSchedule travelschedule = new TravelSchedule();

		travelschedule.setBus(optionalBus.get());

		Station source = travelScheduleDTO.getSource();
		Station destination = travelScheduleDTO.getDestination();
		
		travelschedule.setDestination(destination);

		try {
			travelschedule.setEstimatedArrivalTime(LocalDateTime.parse(travelScheduleDTO.getEstimatedArrivalTime()));
		} catch (DateTimeParseException ex) {
			throw new IllegalArgumentException("Invalid estimated arrival time format: " + ex.getMessage());
		}

		try {
			travelschedule
					.setEstimatedDepartureTime(LocalDateTime.parse(travelScheduleDTO.getEstimatedDepartureTime()));
		} catch (DateTimeParseException ex) {
			throw new IllegalArgumentException("Invalid estimated departure time format: " + ex.getMessage());
		}

		double fareAmount = travelScheduleDTO.getFareAmount();
		if (fareAmount < 0) {
			throw new IllegalArgumentException("Invalid fare amount: " + fareAmount);
		}

		travelschedule.setFareAmount(fareAmount);

		String estimatedArrivalTime = travelScheduleDTO.getEstimatedArrivalTime();
		if (estimatedArrivalTime == null
				|| !estimatedArrivalTime.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}")) {
			throw new IllegalArgumentException("Invalid estimated arrival time: " + estimatedArrivalTime);
		}

		String estimatedDepartureTime = travelScheduleDTO.getEstimatedDepartureTime();
		if (estimatedDepartureTime == null
				|| !estimatedDepartureTime.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}")) {
			throw new IllegalArgumentException("Invalid estimated departure time: " + estimatedDepartureTime);
		}

		travelschedule.setSource(source);

		travelschedule = scheduleRepository.save(travelschedule);
		return travelschedule.getScheduleId() != null;
	}

}
