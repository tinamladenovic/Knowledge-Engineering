package similarity;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimilarComputerFinder {

    private static final Logger logger = Logger.getLogger(SimilarComputerFinder.class.getName());

    public static void main(String[] args) {
        try {
            // Ručno navedi putanju do ontologije (može biti dinamički dodeljena ako koristiš servlete)
            String filePath = "/WEB-INF/resources/ontologyIZ.owx"; // Unesite apsolutnu putanju do ontologije
            
            // Kreiramo instancu ComputerOntologyDAO sa apsolutnom putanjom
            ComputerOntologyDAO ontologyDAO = new ComputerOntologyDAO(new File(filePath));
            CaseRetrieval caseRetrieval = new CaseRetrieval(ontologyDAO);

            // Primer kako pronaći najsličniji računar zadatom računaru
            OWLNamedIndividual targetComputer = ontologyDAO.getIndividual("SomeComputerInstance");

            if (targetComputer != null) {
                // Pronalazi najsličniji računar
                OWLNamedIndividual mostSimilar = caseRetrieval.findMostSimilar(targetComputer);

                if (mostSimilar != null) {
                    // Ispisujemo detalje sličnog računara
                    System.out.println("Most similar computer: " + ontologyDAO.getComputerDetails(mostSimilar));
                } else {
                    logger.log(Level.INFO, "No similar computers found.");
                }
            } else {
                logger.log(Level.WARNING, "Target computer instance not found.");
            }
        } catch (OWLOntologyCreationException e) {
            logger.log(Level.SEVERE, "Error creating ontology: " + e.getMessage(), e);
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, "Ontology file not found: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An unexpected error occurred: " + e.getMessage(), e);
        }
    }
}
