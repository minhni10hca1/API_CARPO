package service;

import java.util.List;

import org.springframework.stereotype.Service;

import model.Car;
import model.Tracking;

@Service
public interface CarGroupService {
	org.bson.Document getCarGroupByLeaderId(String user_id);


}
