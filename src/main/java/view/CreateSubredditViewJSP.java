/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import entity.Subreddit;
import logic.SubredditLogic;

import logic.LogicFactory;

/**
 *
 * @author samderlust
 */
@WebServlet(name = "CreateSubredditViewJSP", urlPatterns = { "/CreateSubredditViewJSP" })
public class CreateSubredditViewJSP extends HttpServlet {
  private SubredditLogic logic = LogicFactory.getFor("Subreddit");

  /**
   * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
   * methods.
   *
   * @param request  servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException      if an I/O error occurs
   */
  protected void processRequest(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    request.setAttribute("columns", logic.getColumnNames());
    request.setAttribute("title", "Create Subreddit");
    request.getRequestDispatcher("/jsp/CreateSubredditView.jsp").forward(request, response);

  }

  // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the
  // + sign on the left to edit the code.">
  /**
   * Handles the HTTP <code>GET</code> method.
   *
   * @param request  servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException      if an I/O error occurs
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    processRequest(request, response);
  }

  /**
   * Handles the HTTP <code>POST</code> method.
   *
   * @param request  servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException      if an I/O error occurs
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String url = request.getParameter(SubredditLogic.URL);
    System.out.println(request.getParameterMap().toString());

    try {

      if (logic.getSubredditWithUrl(url) == null) {
        System.out.println("before add");

        Subreddit entity = logic.createEntity(request.getParameterMap());

        logic.add(entity);
        System.out.println("after add");
        response.sendRedirect("SubredditTableViewJSP");
      }
    } catch (NullPointerException e) {
      request.setAttribute("error", "Host not found");
      processRequest(request, response);
    } catch (Exception e) {
      request.setAttribute("error", e.getMessage());
      processRequest(request, response);
    }
  }

  /**
   * Returns a short description of the servlet.
   *
   * @return a String containing servlet description
   */
  @Override
  public String getServletInfo() {
    return "Short description";
  }// </editor-fold>

}
