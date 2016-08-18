package algorithm;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.Utils;

/**
 * Servlet implementation class RunALS
 */
@WebServlet("/RunALS")
public class RunALS extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RunALS() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		<input> <output> <train_percent> <ranks> <lambda> <iteration>
		String input = request.getParameter("input");
		String output = Utils.output;
		String train_percent = request.getParameter("train_percent");
		String ranks = request.getParameter("ranks");
		String lambda = request .getParameter("lambda");
		String iteration = request.getParameter("iteration");
		
		boolean flag =false;
		try{
			RunSpark.runALS(input, output, train_percent, ranks, lambda, iteration);
		}catch(Exception e){
			flag= false;
			e.printStackTrace();
		}
		
		StringBuffer buffer = new StringBuffer();
		if(flag){//读取输出目录，把输出写入到
			String rmse = Utils.readHDFS(Utils.RMSEPATH);
			buffer.append("<br>").append("模型误差："+rmse);
		}else{
			buffer.append("<br>").append("调用失败！");
		}
		
		// 打印输出
		response.setHeader("content-type","text/html;charset=UTF-8");
		response.getWriter().append("Served at: ").append(request.getContextPath())
		.append(buffer);
		
		
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
