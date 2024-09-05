package similarity;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Set;
import java.util.stream.Collectors;

public class ComputerOntologyDAO {
    private OWLOntology ontology;
    private OWLDataFactory dataFactory;
    private OWLOntologyManager manager;

    // Konstruktor koji prihvata File
    public ComputerOntologyDAO(File file) throws OWLOntologyCreationException, FileNotFoundException {
        manager = OWLManager.createOWLOntologyManager();
        
        System.out.println("Looking for ontology at: " + file.getAbsolutePath());
        
        if (!file.exists()) {
            throw new FileNotFoundException("Ontology file not found at: " + file.getAbsolutePath());
        }

        // Učitaj ontologiju
        try {
            ontology = manager.loadOntologyFromOntologyDocument(file);
            dataFactory = manager.getOWLDataFactory();
        } catch (OWLOntologyCreationException e) {
            System.err.println("Failed to load ontology: " + e.getMessage());
            throw e;
        }
    }

    public void addComputerInstance(String instanceName, String cpuType, String memoryType, String gpuType) {
        OWLNamedIndividual computerInstance = dataFactory.getOWLNamedIndividual(IRI.create("http://www.semanticweb.org/nina/ontologies/2024/6/untitled-ontology-8#" + instanceName));

        OWLNamedIndividual cpuInstance = dataFactory.getOWLNamedIndividual(IRI.create("http://www.semanticweb.org/nina/ontologies/2024/6/untitled-ontology-8#" + cpuType));
        OWLNamedIndividual memoryInstance = dataFactory.getOWLNamedIndividual(IRI.create("http://www.semanticweb.org/nina/ontologies/2024/6/untitled-ontology-8#" + memoryType));
        OWLNamedIndividual gpuInstance = dataFactory.getOWLNamedIndividual(IRI.create("http://www.semanticweb.org/nina/ontologies/2024/6/untitled-ontology-8#" + gpuType));

        // Dodavanje instance računara u ontologiju
        OWLAxiom axiom = dataFactory.getOWLClassAssertionAxiom(dataFactory.getOWLClass(IRI.create("http://www.semanticweb.org/nina/ontologies/2024/6/untitled-ontology-8#Desktop")), computerInstance);
        manager.addAxiom(ontology, axiom);

        // Povezivanje svojstava kao što su "hasCPU", "hasRAM", "hasGPU"
        linkIndividualToProperty(computerInstance, cpuInstance, "hasCPU");
        linkIndividualToProperty(computerInstance, memoryInstance, "hasRAM");
        linkIndividualToProperty(computerInstance, gpuInstance, "hasGPU");

        saveOntology();
    }

    private void linkIndividualToProperty(OWLNamedIndividual subject, OWLNamedIndividual object, String propertyName) {
        OWLObjectProperty property = dataFactory.getOWLObjectProperty(IRI.create("http://www.semanticweb.org/nina/ontologies/2024/6/untitled-ontology-8#" + propertyName));
        OWLObjectPropertyAssertionAxiom axiom = dataFactory.getOWLObjectPropertyAssertionAxiom(property, subject, object);
        manager.addAxiom(ontology, axiom);
    }

    public void saveOntology() {
        try {
            manager.saveOntology(ontology);
        } catch (OWLOntologyStorageException e) {
            System.err.println("Error saving ontology: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Set<OWLNamedIndividual> getAllComputers() {
        OWLClass pcClass = dataFactory.getOWLClass(IRI.create("http://www.semanticweb.org/nina/ontologies/2024/6/untitled-ontology-8#PC"));

        // Сет за чување свих инстанци
        Set<OWLNamedIndividual> allComputers = getIndividualsOfClass(pcClass);

        // Претражујемо и подкласе класе PC (нпр. Desktop и Laptop)
        ontology.getSubClassAxiomsForSuperClass(pcClass).forEach(subClassAxiom -> {
            OWLClass subClass = subClassAxiom.getSubClass().asOWLClass();
            allComputers.addAll(getIndividualsOfClass(subClass));
        });

        return allComputers;
    }

    private Set<OWLNamedIndividual> getIndividualsOfClass(OWLClass owlClass) {
        return ontology.getClassAssertionAxioms(owlClass)
                .stream()
                .map(axiom -> axiom.getIndividual().asOWLNamedIndividual())
                .collect(Collectors.toSet());
    }


    public OWLDataFactory getOWLDataFactory() {
        return dataFactory;
    }

    public OWLNamedIndividual getRelatedIndividual(OWLNamedIndividual subject, OWLObjectProperty property) {
        return ontology.getObjectPropertyAssertionAxioms(subject)
                .stream()
                .filter(axiom -> axiom.getProperty().equals(property))
                .map(OWLObjectPropertyAssertionAxiom::getObject)
                .filter(OWLNamedIndividual.class::isInstance)
                .map(OWLNamedIndividual.class::cast)
                .findFirst()
                .orElse(null);
    }

    public OWLNamedIndividual getIndividual(String individualName) {
        return ontology.getIndividualsInSignature()
                .stream()
                .filter(ind -> ind.getIRI().toString().endsWith(individualName))
                .findFirst()
                .orElse(null);
    }

    public String getComputerDetails(OWLNamedIndividual individual) {
        StringBuilder details = new StringBuilder();

        // Get the individual's IRI and extract the short form (part after the #)
        String individualName = getShortForm(individual.getIRI());
        details.append("Computer Name: ").append(individualName).append("\n");

        // Get related properties such as hasCPU, hasRAM, and hasGPU
        OWLObjectProperty hasCPU = dataFactory.getOWLObjectProperty(IRI.create("http://www.semanticweb.org/nina/ontologies/2024/6/untitled-ontology-8#hasCPU"));
        OWLObjectProperty hasRAM = dataFactory.getOWLObjectProperty(IRI.create("http://www.semanticweb.org/nina/ontologies/2024/6/untitled-ontology-8#hasRAM"));
        OWLObjectProperty hasGPU = dataFactory.getOWLObjectProperty(IRI.create("http://www.semanticweb.org/nina/ontologies/2024/6/untitled-ontology-8#hasGPU"));

        OWLNamedIndividual cpu = getRelatedIndividual(individual, hasCPU);
        OWLNamedIndividual ram = getRelatedIndividual(individual, hasRAM);
        OWLNamedIndividual gpu = getRelatedIndividual(individual, hasGPU);

        if (cpu != null) {
            details.append("CPU: ").append(getShortForm(cpu.getIRI())).append("\n");
        }
        if (ram != null) {
            details.append("RAM: ").append(getShortForm(ram.getIRI())).append("\n");
        }
        if (gpu != null) {
            details.append("GPU: ").append(getShortForm(gpu.getIRI())).append("\n");
        }

        return details.toString();
    }

    private String getShortForm(IRI iri) {
        String iriString = iri.toString();
        // Extract part after the # symbol
        return iriString.substring(iriString.indexOf('#') + 1);
    }


}
