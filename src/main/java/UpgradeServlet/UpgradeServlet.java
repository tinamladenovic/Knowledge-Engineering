package UpgradeServlet;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.HermiT.ReasonerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;

@WebServlet("/UpgradeServlet")
public class UpgradeServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private OWLReasoner reasoner;
    private OWLDataFactory dataFactory;

    @Override
    public void init() throws ServletException {
        try {
            // Inicijalizacija reasoner-a i dataFactory-ja prilikom pokretanja servleta
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File ontologyFile = new File(getServletContext().getRealPath("/WEB-INF/resources/ontologyIZ.owx"));
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(ontologyFile);
            dataFactory = manager.getOWLDataFactory();  // Inicijalizacija dataFactory
            reasoner = new ReasonerFactory().createReasoner(ontology);  // Inicijalizacija reasoner

            // Proverite da li je ontologija uspešno učitana
            if (ontology != null) {
                System.out.println("Ontologija uspešno učitana.");
            } else {
                System.out.println("Ontologija nije učitana.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Set<String> motherboards = new HashSet<>();

        try {
            OWLClass motherboardClass = dataFactory.getOWLClass(IRI.create("http://www.semanticweb.org/nina/ontologies/2024/6/untitled-ontology-8#MotherBoard"));
            Set<OWLNamedIndividual> motherboardIndividuals = reasoner.getInstances(motherboardClass, false).getFlattened();

            // Ispis svih matičnih ploča u konzoli
            System.out.println("Matične ploče iz ontologije:");
            for (OWLNamedIndividual motherboard : motherboardIndividuals) {
                String motherboardName = motherboard.getIRI().getFragment();
                motherboards.add(motherboardName);
                // Ispis u konzoli
                System.out.println(" - " + motherboardName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        request.setAttribute("motherboards", motherboards);
        RequestDispatcher dispatcher = request.getRequestDispatcher("upgradePage.jsp");
        dispatcher.forward(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        String selectedMotherboard = request.getParameter("motherboard");

        String currentMemoryStr = request.getParameter("currentMemoryCapacity");

        if (selectedMotherboard == null || currentMemoryStr == null || currentMemoryStr.isEmpty()) {
            request.setAttribute("error", "Please select a motherboard and enter the current memory capacity.");
            doGet(request, response);
            return;
        }

        int currentMemoryCapacity;
        try {
            currentMemoryCapacity = Integer.parseInt(currentMemoryStr);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid memory capacity entered.");
            doGet(request, response);
            return;
        }

        // Pronađi kompatibilne memorijske module
        Set<String> upgradeModules = findCompatibleMemoryModules(selectedMotherboard, currentMemoryCapacity);

        if (upgradeModules.isEmpty()) {
            request.setAttribute("error", "No upgrade modules found for the given motherboard and memory capacity.");
        }

        request.setAttribute("motherboard", selectedMotherboard);
        request.setAttribute("currentMemoryCapacity", currentMemoryCapacity);
        request.setAttribute("upgradeModules", upgradeModules);

        RequestDispatcher dispatcher = request.getRequestDispatcher("upgradePage.jsp");
        dispatcher.forward(request, response);
    }


    private Set<String> findCompatibleMemoryModules(String motherboard, int currentMemoryCapacity) {
        Set<String> compatibleModules = new HashSet<>();

        try {
            // Pronađi odabranu matičnu ploču
            OWLNamedIndividual motherboardIndividual = dataFactory.getOWLNamedIndividual(IRI.create("http://www.semanticweb.org/nina/ontologies/2024/6/untitled-ontology-8#" + motherboard));
            
            // Dobijanje RAM tipa pomoću svojstva hasRAMType
            OWLObjectProperty hasRAMType = dataFactory.getOWLObjectProperty(IRI.create("http://www.semanticweb.org/nina/ontologies/2024/6/untitled-ontology-8#hasRAMType"));
            NodeSet<OWLNamedIndividual> ramTypeSlots = reasoner.getObjectPropertyValues(motherboardIndividual, hasRAMType);

            String ramSlotType = "";
            if (!ramTypeSlots.isEmpty()) {
                ramSlotType = ramTypeSlots.getFlattened().iterator().next().getIRI().getFragment();  // Npr. DIMM, SODIMM
            }

            if (ramSlotType.isEmpty()) {
                return compatibleModules;  // Ako nema RAM tipa, vraća praznu listu
            }

            // Filtriraj memorijske module na osnovu RAM slota i kapaciteta
            OWLClass memoryModulClass = dataFactory.getOWLClass(IRI.create("http://www.semanticweb.org/nina/ontologies/2024/6/untitled-ontology-8#MemoryModul"));
            NodeSet<OWLNamedIndividual> memoryModules = reasoner.getInstances(memoryModulClass, false);

            for (OWLNamedIndividual module : memoryModules.getFlattened()) {
                String memoryName = module.getIRI().getFragment();
                if (memoryName.contains(ramSlotType)) {  // Filtriraj module po RAM slot tipu (DIMM ili SODIMM)
                    int moduleCapacity = extractCapacityFromMemoryName(memoryName);

                    // Dodaj samo module koji imaju veći kapacitet od trenutnog
                    if (moduleCapacity > currentMemoryCapacity) {
                        compatibleModules.add(memoryName + " (" + moduleCapacity + " GB)");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return compatibleModules;
    }


    private int extractCapacityFromMemoryName(String memoryName) {
        // Ekstrakcija brojeva iz naziva, npr. "DDR4_32" -> 32
        String numberOnly = memoryName.replaceAll("[^0-9]", "");  // Uklanjamo sve osim brojeva
        return numberOnly.isEmpty() ? 0 : Integer.parseInt(numberOnly);  // Vraća broj, ili 0 ako nema brojeva
    }
}
