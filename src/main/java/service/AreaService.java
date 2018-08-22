package service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import model.Campaign_CM;
import model.Car;
import model.Tracking;

@Service
public interface AreaService {
	//Car getInfoCarByUserId(String user_id);
	org.bson.Document getInfoAreaByCode(String code);
}
