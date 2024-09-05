package SimilarityServlet;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import similarity.CaseRetrieval;
import similarity.ComputerOntologyDAO;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/SimilarityServlet")
public class SimilarityServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private ComputerOntologyDAO ontologyDAO;
    private CaseRetrieval caseRetrieval;

    @Override
    public void init() throws ServletException {
        try {
            // Nabavljanje putanje do fajla unutar WEB-INF direktorijuma
            ServletContext context = getServletContext();
            String ontologyPath = context.getRealPath("/WEB-INF/resources/ontologyIZ.owx");

            File ontologyFile = new File(ontologyPath);
            if (!ontologyFile.exists()) {
                throw new ServletException("Ontology file not found at " + ontologyPath);
            }

            // Inicijalizacija DAO-a sa fajlom
            ontologyDAO = new ComputerOntologyDAO(ontologyFile);
            caseRetrieval = new CaseRetrieval(ontologyDAO); // Inicijalizacija za poređenje slučajeva
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Dobijanje unetih specifikacija za poređenje
            String cpuCores = request.getParameter("cpuCores");
            String memorySize = request.getParameter("memorySize");
            String gpuModel = request.getParameter("gpuModel");

            // Proverite da li su svi parametri prisutni
            if (cpuCores == null || memorySize == null || gpuModel == null) {
                StringBuilder missingParams = new StringBuilder("Missing parameters: ");
                if (cpuCores == null) missingParams.append("cpuCores ");
                if (memorySize == null) missingParams.append("memorySize ");
                if (gpuModel == null) missingParams.append("gpuModel ");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, missingParams.toString());
                return;
            }

            // Kreiranje ili pronalaženje računara bazirano na unetim specifikacijama
            ontologyDAO.addComputerInstance("targetComputer", cpuCores, memorySize, gpuModel);
            OWLNamedIndividual targetComputer = ontologyDAO.getIndividual("targetComputer");
            System.out.println("Target Computer: " + targetComputer.getIRI());

            // Pronalazak sličnog računara
            List<OWLNamedIndividual> similarComputers = caseRetrieval.findSimilarComputers(targetComputer);

            // Postavljanje rezultata kao HTML u odgovoru
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();

            out.println("<html><head><title>Similar Computers</title>");
            out.println("<style>");
            out.println("body {font-family: Arial, sans-serif;}");
            out.println(".computer-list {margin-top: 20px;}");
            out.println(".computer-item {padding: 10px; background-color: #f0f0f0; margin-bottom: 10px; border-radius: 5px;}");
            out.println("a {text-decoration: none; color: #007BFF;}");
            out.println("</style></head><body>");
            out.println("<h2>Similar Computers:</h2>");

            if (similarComputers != null && !similarComputers.isEmpty()) {
                out.println("<div class='computer-list'>");
                for (OWLNamedIndividual computer : similarComputers) {
                    out.println("<div class='computer-item'>");
                    out.println("<strong>Computer: </strong>" + getShortForm(computer.getIRI()));
                    // Možeš dodati više detalja ovde, npr. CPU, RAM, GPU:
                    out.println("<br><strong>Details: </strong>" + ontologyDAO.getComputerDetails(computer)); 
                    out.println("</div>");
                }
                out.println("</div>");
            } else {
                out.println("<p>No similar computers found.</p>");
            }

            out.println("<a href='findSimilarPage.jsp'>Back to Search</a>");
            out.println("</body></html>");
        } catch (Exception e) {
            e.printStackTrace(); // Dodajte logovanje greške
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error: " + e.getMessage());
        }
    }

    private String getShortForm(IRI iri) {
        // Uklanjanje prefiksa iz IRI-a da bi se dobio lepši ispis
        String iriString = iri.toString();
        int index = iriString.lastIndexOf('#');
        if (index != -1) {
            return iriString.substring(index + 1);
        }
        return iriString;
    }
}
