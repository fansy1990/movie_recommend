package algorithm;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet implementation class Monitor
 */
@WebServlet("/Monitor")
public class Monitor extends HttpServlet {
	private Logger log = LoggerFactory.getLogger(getClass());
	private static final long serialVersionUID = 1L;
	
	private static int progress=1;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Monitor() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		log.info("进度："+progress);
		if(progress>=100){
			log.info("算法已结束");
			progress =0;
		}
		// 打印输出
		PrintWriter out = response.getWriter();
		++progress;
		out.write(progress+"");
		out.flush();
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
