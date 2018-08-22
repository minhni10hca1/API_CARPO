package controller;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.jws.soap.SOAPBinding.Use;
import javax.ws.rs.POST;

import model.Car;
import model.ConfirmCarStatus;
import model.Global;
import model.GlobalErrorCode;
import model.GlobalMessageScreen;
import model.JsonFormatError;
import model.JsonFormatTemplate;
import model.LocationGoogle;
import model.Report;
import model.Tracking;
import model.User;
import model_json.distance_list;
import model_json.earning_list;
import model_json.formatJsonHistoryTrackingDataInfo;
import model_json.formatJsonTrackingInfo;

import org.apache.hadoop.hdfs.server.namenode.status_jsp;
import org.apache.hadoop.security.authentication.server.AuthenticationToken;
import org.hibernate.annotations.NotFound;
import org.omg.PortableServer.CurrentPackage.NoContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.resource.JwtAccessTokenConverterConfigurer;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.core.JsonParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mysql.fabric.Response;

import service.CarService;
import service.ConfirmCarStatusService;
import service.ReportService;
import service.TrackingService;
import util.FunctionUtil;
import util.GlobalUtils;

@RestController
public class ConfirmCarStatusController {

	@Autowired
	ConfirmCarStatusService service;

	public static String result_sms = "";
	public static Boolean status_result = false;

	private static BufferedImage resize(BufferedImage img, int height, int width) {
		Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage resized = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = resized.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();
		return resized;
	}

	private BufferedImage resizeImageWithHint(BufferedImage originalImage,
			int type, int IMG_WIDTH, int IMG_HEIGHT) {

		BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT,
				type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
		g.dispose();
		g.setComposite(AlphaComposite.Src);

		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		return resizedImage;
	}

	private void saveImageReport(String url_img, String img_name,
			String base_64_img) {
		try {
			String url = "";
			String[] arrImg = base_64_img.split(",");
			if (arrImg.length > 1)
				base_64_img = arrImg[1];
			byte[] decodedImg = Base64.getMimeDecoder().decode(
					base_64_img.getBytes(StandardCharsets.UTF_8));
			Path destinationFile = Paths.get(url_img, img_name
					+ Global.format_img);
			try {
				Files.write(destinationFile, decodedImg);
				// resize img
				String duongdan = destinationFile.toString();
				File input = new File(destinationFile.toString());
				BufferedImage image = ImageIO.read(input);
				int type = image.getType() == 0? BufferedImage.TYPE_INT_ARGB : image.getType();
				BufferedImage resizeImageHintJpg = resizeImageWithHint(image, type, 350, 350);
				ImageIO.write(resizeImageHintJpg, "jpg", input);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.print("error");
				e.printStackTrace();

			}
		} catch (Exception ex) {
			System.out.print("error:" + ex.getMessage());

		}
	}

	@RequestMapping(value = "/insert-confirm-car-status", method = POST)
	public String insertConfirmCarStatus(
			@RequestParam(required = false) String user_id,
			@RequestParam(required = false) String image) {
		if (user_id == null || image == null)
			return FunctionUtil.getJsonErrorForRequestParams();
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
		ConfirmCarStatus confirmCarStatus = new ConfirmCarStatus();
		confirmCarStatus.setUser_id(user_id);
		// process save image
		Date today = Calendar.getInstance().getTime();
		String nameImage = user_id + "_"
				+ GlobalUtils.convertStringToTime_SaveImg(today);
		FunctionUtil
				.createdForderReportIfNotExits(Global.url_image_confirm_car_status);
		saveImageReport(Global.url_image_confirm_car_status, nameImage, image);
		confirmCarStatus.setImage(Global.host_domain
				+ Global.mapping_image_confirm_car_status + "/" + nameImage
				+ Global.format_img);
		confirmCarStatus
				.setCreated_date(GlobalUtils.convertStringToDate(today));
		confirmCarStatus
				.setCreated_time(GlobalUtils.convertStringToTime(today));
		Boolean result = service.insertConfirmCarStatus(confirmCarStatus);
		if (result) {
			formatTemplate.setStatus(Global.status_ok);
			formatTemplate.setData("");
		} else {
			formatTemplate.setStatus(Global.status_fail);
			error.setCode(GlobalErrorCode.error_code_insert_data);
			error.setMessage(GlobalMessageScreen.confirm_car_unsuccessfull);
			formatTemplate.setError(error);
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(formatTemplate);
		return json;
	}

	@RequestMapping(value = Global.mapping_image_confirm_car_status
			+ "/{imgName}", method = GET)
	public ResponseEntity<byte[]> viewImageConfirmCarStatus(
			@PathVariable("imgName") String imgName) {
		RandomAccessFile f;
		try {
			f = new RandomAccessFile(Global.url_image_confirm_car_status + "/"
					+ imgName + Global.format_img, "r");
			byte[] b = new byte[(int) f.length()];
			f.readFully(b);
			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.IMAGE_PNG);
			return new ResponseEntity<byte[]>(b, headers, HttpStatus.CREATED);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
	
	@RequestMapping(value = "/upload-photo-car-status", method = POST)
    public String uploadPhotoCarStatus(@RequestParam(required = false) String user_id,
    		@RequestParam(value = "file") MultipartFile fileImg) throws IOException {
		
//		byte[] decodedImg = fileImg.getBytes();
//		Path destinationFile = Paths.get(Global.url_image_confirm_car_status, "abc"
//				+ Global.format_img);
//		try {
//			Files.write(destinationFile, decodedImg);
//
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			System.out.print("error");
//			e.printStackTrace();
//
//		}
		if (user_id == null || fileImg == null)
			return FunctionUtil.getJsonErrorForRequestParams();
		user_id = user_id.replace('"', ' ').trim();
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
		ConfirmCarStatus confirmCarStatus = new ConfirmCarStatus();
		confirmCarStatus.setUser_id(user_id);
		// process save image
		Date today = Calendar.getInstance().getTime();
		String nameImage = user_id + "_"
				+ GlobalUtils.convertStringToTime_SaveImg(today);
		FunctionUtil
				.createdForderReportIfNotExits(Global.url_image_confirm_car_status);
		saveImageReportNew(Global.url_image_confirm_car_status, nameImage, fileImg);
		confirmCarStatus.setImage(Global.host_domain
				+ Global.mapping_image_confirm_car_status + "/" + nameImage
				+ Global.format_img);
		confirmCarStatus
				.setCreated_date(GlobalUtils.convertStringToDate(today));
		confirmCarStatus
				.setCreated_time(GlobalUtils.convertStringToTime(today));
		Boolean result = service.insertConfirmCarStatus(confirmCarStatus);
		if (result) {
			formatTemplate.setStatus(Global.status_ok);
			formatTemplate.setData("");
		} else {
			formatTemplate.setStatus(Global.status_fail);
			error.setCode(GlobalErrorCode.error_code_insert_data);
			error.setMessage(GlobalMessageScreen.confirm_car_unsuccessfull);
			formatTemplate.setError(error);
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(formatTemplate);
		return json;
		
 
    }
	
	
	@RequestMapping(value = "/upload-photo-car-status-using-tool", method = POST)
    public String uploadPhotoCarStatusUsingTool(@RequestParam(required = false) String user_id,
    		@RequestParam(value = "file") MultipartFile fileImg,@RequestParam(required = false) String created_date) throws IOException {
		
		if (user_id == null || fileImg == null || created_date == null)
			return FunctionUtil.getJsonErrorForRequestParams();
		user_id = user_id.replace('"', ' ').trim();
		JsonFormatTemplate formatTemplate = new JsonFormatTemplate();
		JsonFormatError error = new JsonFormatError();
		ConfirmCarStatus confirmCarStatus = new ConfirmCarStatus();
		confirmCarStatus.setUser_id(user_id);
		// process save image
		Date today = Calendar.getInstance().getTime();
		String nameImage = user_id + "_"
				+ GlobalUtils.convertStringToTime_SaveImg(today);
		FunctionUtil
				.createdForderReportIfNotExits(Global.url_image_confirm_car_status);
		saveImageReportNew(Global.url_image_confirm_car_status, nameImage, fileImg);
		confirmCarStatus.setImage(Global.host_domain
				+ Global.mapping_image_confirm_car_status + "/" + nameImage
				+ Global.format_img);
		confirmCarStatus
				.setCreated_date(created_date);
		confirmCarStatus
				.setCreated_time(GlobalUtils.convertStringToTime(today));
		Boolean result = service.insertConfirmCarStatus(confirmCarStatus);
		if (result) {
			formatTemplate.setStatus(Global.status_ok);
			formatTemplate.setData("");
		} else {
			formatTemplate.setStatus(Global.status_fail);
			error.setCode(GlobalErrorCode.error_code_insert_data);
			error.setMessage(GlobalMessageScreen.confirm_car_unsuccessfull);
			formatTemplate.setError(error);
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(formatTemplate);
		return json;
		
 
    }
	
	private void saveImageReportNew(String url_img, String img_name,
			MultipartFile fileImg) {
		try {
			byte[] decodedImg = fileImg.getBytes();
			Path destinationFile = Paths.get(url_img, img_name
					+ Global.format_img);
			try {
				Files.write(destinationFile, decodedImg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.print("error");
				e.printStackTrace();
			}
		} catch (Exception ex) {
			System.out.print("error:" + ex.getMessage());

		}
	}

}
