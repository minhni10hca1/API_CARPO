package service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import model.Car;
import model.Report;
import model.Tracking;

@Service
public interface ReportService {
	//Car getInfoCarByUserId(String user_id);
	String insertReport(Report report);
}
