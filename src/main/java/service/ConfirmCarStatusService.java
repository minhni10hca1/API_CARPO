package service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import model.ConfirmCarStatus;
import model.LocationGoogle;
import model.Tracking;

@Service
public interface ConfirmCarStatusService {
	Boolean insertConfirmCarStatus(ConfirmCarStatus confirmCarStatus);
	Boolean deleteConfirmCarStatusByImg(String img);

}
