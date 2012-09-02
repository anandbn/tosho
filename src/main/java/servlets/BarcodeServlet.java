package servlets;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import utils.BarcodeGenerator;

import java.io.IOException;
import java.text.MessageFormat;



public class BarcodeServlet extends HttpServlet {

    public void init() throws ServletException {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String barCodeSrc = request.getParameter("code");
        try {
            BarcodeGenerator.generateCodeToStream(response.getOutputStream(), 
                                                  String.format("%012d",barCodeSrc ), 
                                                  BarcodeFormat.UPC_A, 30, 100, "png");
        } catch (WriterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}