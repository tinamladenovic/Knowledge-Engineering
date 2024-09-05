package similarity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

public class CaseRetrieval {

    private ComputerOntologyDAO ontologyDAO;

    // Stroži težinski faktori za komponente
    private static final double CPU_WEIGHT = 0.3;
    private static final double MEMORY_WEIGHT = 0.5;  // RAM je sada važniji
    private static final double GPU_WEIGHT = 0.2;
    private static final double SIMILARITY_THRESHOLD = 0.5;  // Povećan prag sličnosti

    public CaseRetrieval(ComputerOntologyDAO ontologyDAO) {
        this.ontologyDAO = ontologyDAO;
    }

    public double compareComputers(OWLNamedIndividual computer1, OWLNamedIndividual computer2) {
        double similarity = 0.0;

        System.out.println("Comparing computers: " + computer1.getIRI() + " with " + computer2.getIRI());

        // Upotreba samo simboličkog poređenja bez numeričkih podataka
        similarity += compareSymbolicComponent(computer1, computer2, "hasCPU", CPU_WEIGHT);
        similarity += compareSymbolicComponent(computer1, computer2, "hasRAM", MEMORY_WEIGHT);
        similarity += compareSymbolicComponent(computer1, computer2, "hasGPU", GPU_WEIGHT);

        System.out.println("Total similarity: " + similarity);
        return similarity;
    }

    // Metod za poređenje simboličkih osobina
    private double compareSymbolicComponent(OWLNamedIndividual computer1, OWLNamedIndividual computer2, String propertyName, double weight) {
        OWLObjectProperty property = ontologyDAO.getOWLDataFactory().getOWLObjectProperty(IRI.create("http://www.semanticweb.org/nina/ontologies/2024/6/untitled-ontology-8#" + propertyName));
        OWLNamedIndividual component1 = ontologyDAO.getRelatedIndividual(computer1, property);
        OWLNamedIndividual component2 = ontologyDAO.getRelatedIndividual(computer2, property);
        
        if (component1 != null && component2 != null) {
            if (component1.equals(component2)) {
                return weight;  // Potpuno podudaranje
            }
        }
        return 0.0;  // Nema podudaranja
    }

    public List<OWLNamedIndividual> findSimilarComputers(OWLNamedIndividual targetComputer) {
        List<OWLNamedIndividual> similarComputers = new ArrayList<>();
        Set<OWLNamedIndividual> allComputers = ontologyDAO.getAllComputers();

        // Ispis svih računara koji su učitani iz ontologije
        System.out.println("All computers in the ontology:");
        for (OWLNamedIndividual computer : allComputers) {
            System.out.println("Computer: " + computer.getIRI());
        }

        for (OWLNamedIndividual computer : allComputers) {
            if (!computer.equals(targetComputer)) {
                double similarity = compareComputers(targetComputer, computer);
                if (similarity >= SIMILARITY_THRESHOLD) {  
                    similarComputers.add(computer);
                }
            }
        }

        return similarComputers;
    }

    public OWLNamedIndividual findMostSimilar(OWLNamedIndividual targetComputer) {
        OWLNamedIndividual mostSimilar = null;
        double highestSimilarity = 0.0;

        Set<OWLNamedIndividual> allComputers = ontologyDAO.getAllComputers();
        for (OWLNamedIndividual computer : allComputers) {
            if (!computer.equals(targetComputer)) {
                double similarity = compareComputers(targetComputer, computer);
                if (similarity > highestSimilarity) {
                    highestSimilarity = similarity;
                    mostSimilar = computer;
                }
            }
        }

        return mostSimilar;
    }
}
