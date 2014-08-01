package com.lqh.vote;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.lqh.vote.model.Voter;

public class Vote {

	private CloseableHttpClient  httpClient;
	private HttpClientContext context;
	
	static final String SELECTED_PERSON = "6";
	static final Logger logger = Logger.getLogger(Vote.class);
	 
	public Vote() {

		if(httpClient == null) {
			
			RequestConfig globalConfig = RequestConfig.custom()
			        .setCookieSpec(CookieSpecs.BEST_MATCH)
			        .build();
			
			httpClient = HttpClients.custom().setDefaultRequestConfig(globalConfig).build();
		}
		
		if(context == null) {
			context = HttpClientContext.create();
		}
	}
	
	public String getVerifyCode() {
		String verifyCodeURI = "http://vote.jyb.cn/kvkLk_zhl.php?act=act";
		//String verifyCodeURI = "http://jx.axtchild.com/showLogin";
		
		Header[] headers = {
				new BasicHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3"),
				new BasicHeader("Connection", "keep-alive"),
				new BasicHeader("Host", "vote.jyb.cn"),
				new BasicHeader("Referer", "http://vote.jyb.cn/vote2014jsyrkm.html"),
				new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:30.0) Gecko/20100101 Firefox/30.0"),
				new BasicHeader("X-Requested-With", "XMLHttpRequest"),
		};
		
		HttpGet httpGet = new HttpGet(verifyCodeURI);
		httpGet.setHeaders(headers);
		
		//HttpPost httpPost = new HttpPost(verifyCodeURI);
		//httpPost.setHeaders(headers);
		CloseableHttpResponse httpResponse = null;	
		try {
			httpResponse = httpClient.execute(httpGet, context);
			
			List<Cookie> cookies = context.getCookieStore().getCookies();			
			for(Cookie cookie : cookies) {
				System.out.println("获取验证码信息：" + cookie.getName() + " : " + cookie.getValue());
				if(cookie.getName().equals("word")) {
					return cookie.getValue();
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(httpResponse != null) {
					httpResponse.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return "无验证码";
	}
	
	public String submitForm(Voter voter) {
		String submitFormURI = "http://vote.jyb.cn/vote2014jsyrkm_act.php";
		
		Header[] headers = {
				new BasicHeader("Host", "vote.jyb.cn"),
				new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:30.0) Gecko/20100101 Firefox/30.0"),
				new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"),
				new BasicHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3"),
				new BasicHeader("Accept-Encoding", "gzip, deflate"),
				new BasicHeader("Referer", "http://vote.jyb.cn/vote2014jsyrkm.html"),
				new BasicHeader("Cookie", "word=1111; expires=Fri, 30 Jul 2014 11:42:44 GMT; path=/; domain=vote.jyb.cn"),
		};
		
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("person[]", voter.getSelectedPerson()));
		formparams.add(new BasicNameValuePair("name", voter.getName()));
		formparams.add(new BasicNameValuePair("address", voter.getAddress()));
		formparams.add(new BasicNameValuePair("identify", voter.getIdentity()));
		formparams.add(new BasicNameValuePair("post", voter.getPost()));
		formparams.add(new BasicNameValuePair("email", voter.getEmail()));
		formparams.add(new BasicNameValuePair("mobile", voter.getMobile()));
		formparams.add(new BasicNameValuePair("action", voter.getAction()));
		formparams.add(new BasicNameValuePair("cap", voter.getCap()));
		formparams.add(new BasicNameValuePair("提交.x", voter.getPostX()));
		formparams.add(new BasicNameValuePair("提交.y", voter.getPostY()));
		
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
		
		HttpPost httpPost = new HttpPost(submitFormURI);
		httpPost.setHeaders(headers);
		httpPost.setEntity(entity);
		
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(httpPost, context);
			
			HttpEntity en = response.getEntity();
			if(en != null) {
				//<script>alert('验证不正确！');location.href='vote2014jsyrkm.html';</script>
				
				String result = EntityUtils.toString(en, "UTF-8");
				return result;
				
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return "HttpEntity无内容！";
	}
	
	/**
	 * 读取 投票人 信息
	 */
	public List<Voter> readVoters() {
		/*List<Voter> voters = new ArrayList<Voter>();

		String selectedPerson = "6";
		String name = "李清华";
		String address = "河北石家庄";
		String identity = "13112119870624145X";
		String post = "053100";
		String email = "hello@163.com";
		String mobile = "13633115257";
		String action = "post";
		
		Voter voter = new Voter();
		voter.setSelectedPerson(selectedPerson);
		voter.setName(name);
		voter.setAddress(address);
		voter.setIdentity(identity);
		voter.setPost(post);
		voter.setEmail(email);
		voter.setMobile(mobile);
		voter.setAction(action);
		
		voters.add(voter);
		
		return voters;*/
		
		logger.info("start reading voters from excel");		
		ExcelUtil excelUtil = new ExcelUtil(this.getClass().getClassLoader().getResourceAsStream("500.xlsx"));
		List<Voter> voters = excelUtil.readFromExcel();
		for(Voter voter : voters) {
			System.out.println(voter);
		}
		logger.info("end reading voters, count:" + voters.size());
		return voters;
	}
	
	/**
	 * 执行 投票 流程
	 */
	public void process(List<Voter> voters) {
		
		logger.info("process start ....\r\n");
		
		for(Voter voter : voters) {
			
			logger.info(" >>>>>>>>>>>>>>>>>>> " + voter + "start vote\r\n" );
			
			/*logger.info("------------------- start getting verifyCode"); 
			String cap = getVerifyCode();
			logger.info("------------------- end getting verifyCode:" + cap +"\r\n");*/
			
			logger.info("=================== start submitting the form");
			voter.setCap("1111");			
			String result = submitForm(voter);
			logger.info("=================== end submitting the form, result:" + result + "\r\n");
			
			logger.info(" <<<<<<<<<<<<<<<<<<< " + voter + "end vote\r\n");
			
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
		logger.info("process end successfully !!!!");
	}
	
	public static void main(String[] args) {
		logger.info("main start ..\r\n");
		
		Vote vote = new Vote();
		List<Voter> voters = vote.readVoters();
		
		vote.process(voters);
		
		logger.info("main end  ..");
	}
	
	class ExcelUtil {
		
		//excel 读入流
		InputStream is;
		
		ExcelUtil(InputStream is) {
			this.is = is;
		}
		
		public List<Voter> readFromExcel() {
			
			List<Voter> voters = new ArrayList<Voter>();
			
			try {
				Workbook wb = WorkbookFactory.create(is);
				Sheet sheet = wb.getSheetAt(0);
				
				//省略检查表头

				// 遍历每行，将每行的信息转为Voter对象，存入到list中				
				int rowNum = sheet.getLastRowNum();
				for (int i = 1; i < rowNum + 1; i++) {
					Row row = sheet.getRow(i);

					Cell name = row.getCell(0);
					if (name != null) {
						name.setCellType(Cell.CELL_TYPE_STRING);
					}

					Cell identity = row.getCell(1);
					if (identity != null) {
						identity.setCellType(Cell.CELL_TYPE_STRING);
					}
					
					Cell phoneNum = row.getCell(2);
					if (phoneNum != null) {
						phoneNum.setCellType(Cell.CELL_TYPE_STRING);
					}
					
					Cell address = row.getCell(3);
					if (address != null) {
						address.setCellType(Cell.CELL_TYPE_STRING);
					}
					
					Cell post = row.getCell(4);
					if (post != null) {
						post.setCellType(Cell.CELL_TYPE_STRING);
					}
					
					Cell email = row.getCell(5);
					if (post != null) {
						post.setCellType(Cell.CELL_TYPE_STRING);
					}
					
					Voter voter = new Voter();
					voter.setExcelId(i);
					voter.setSelectedPerson(Vote.SELECTED_PERSON);
					voter.setName(name.getStringCellValue());
					voter.setAddress(address.getStringCellValue());
					voter.setIdentity(identity.getStringCellValue());
					voter.setPost(post.getStringCellValue());
					voter.setEmail(email.getStringCellValue());
					voter.setMobile(phoneNum.getStringCellValue());
					voter.setAction("post");
					
					if(voters.contains(voter)) {
						logger.info("^^^^^^^重复出现....^^^^^^^^");
					} 
					
					voters.add(voter);
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InvalidFormatException e) {
				e.printStackTrace();
			} finally {
				try {
					if (is != null) {
						is.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return voters;
		}
	}
	
}
