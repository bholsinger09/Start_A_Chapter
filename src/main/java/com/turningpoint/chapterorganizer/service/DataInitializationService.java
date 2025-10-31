package com.turningpoint.chapterorganizer.service;

import com.turningpoint.chapterorganizer.entity.Institution;
import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.repository.InstitutionRepository;
import com.turningpoint.chapterorganizer.repository.ChapterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializationService implements CommandLineRunner {

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private ChapterRepository chapterRepository;

    @Override
    public void run(String... args) throws Exception {
        // Only initialize if no institutions exist
        if (institutionRepository.count() == 0) {
            initializeInstitutions();
        }
        
        // Only initialize if we have fewer than 10 chapters
        if (chapterRepository.count() < 10) {
            initializeChapters();
        }
    }

    private void initializeInstitutions() {
        Institution[] institutions = {
            new Institution("Harvard University", "University", "Cambridge", "MA", "USA"),
            new Institution("Stanford University", "University", "Stanford", "CA", "USA"),
            new Institution("Massachusetts Institute of Technology", "University", "Cambridge", "MA", "USA"),
            new Institution("University of California, Berkeley", "University", "Berkeley", "CA", "USA"),
            new Institution("University of Michigan", "University", "Ann Arbor", "MI", "USA"),
            new Institution("Yale University", "University", "New Haven", "CT", "USA"),
            new Institution("Princeton University", "University", "Princeton", "NJ", "USA"),
            new Institution("Columbia University", "University", "New York", "NY", "USA"),
            new Institution("University of Chicago", "University", "Chicago", "IL", "USA"),
            new Institution("University of Pennsylvania", "University", "Philadelphia", "PA", "USA"),
            new Institution("Cornell University", "University", "Ithaca", "NY", "USA"),
            new Institution("Northwestern University", "University", "Evanston", "IL", "USA"),
            new Institution("Duke University", "University", "Durham", "NC", "USA"),
            new Institution("Dartmouth College", "College", "Hanover", "NH", "USA"),
            new Institution("Brown University", "University", "Providence", "RI", "USA"),
            new Institution("University of Southern California", "University", "Los Angeles", "CA", "USA"),
            new Institution("Carnegie Mellon University", "University", "Pittsburgh", "PA", "USA"),
            new Institution("Vanderbilt University", "University", "Nashville", "TN", "USA"),
            new Institution("Rice University", "University", "Houston", "TX", "USA"),
            new Institution("Emory University", "University", "Atlanta", "GA", "USA"),
            new Institution("Georgetown University", "University", "Washington", "DC", "USA"),
            new Institution("University of Notre Dame", "University", "Notre Dame", "IN", "USA"),
            new Institution("Washington University in St. Louis", "University", "St. Louis", "MO", "USA"),
            new Institution("University of California, Los Angeles", "University", "Los Angeles", "CA", "USA"),
            new Institution("University of Virginia", "University", "Charlottesville", "VA", "USA"),
            new Institution("University of North Carolina at Chapel Hill", "University", "Chapel Hill", "NC", "USA"),
            new Institution("Wake Forest University", "University", "Winston-Salem", "NC", "USA"),
            new Institution("Tufts University", "University", "Medford", "MA", "USA"),
            new Institution("New York University", "University", "New York", "NY", "USA"),
            new Institution("Boston College", "College", "Chestnut Hill", "MA", "USA"),
            new Institution("Boston University", "University", "Boston", "MA", "USA"),
            new Institution("Northeastern University", "University", "Boston", "MA", "USA"),
            new Institution("University of Rochester", "University", "Rochester", "NY", "USA"),
            new Institution("Brandeis University", "University", "Waltham", "MA", "USA"),
            new Institution("Case Western Reserve University", "University", "Cleveland", "OH", "USA"),
            new Institution("Tulane University", "University", "New Orleans", "LA", "USA"),
            new Institution("University of Miami", "University", "Coral Gables", "FL", "USA"),
            new Institution("Lehigh University", "University", "Bethlehem", "PA", "USA"),
            new Institution("Pepperdine University", "University", "Malibu", "CA", "USA"),
            new Institution("University of California, San Diego", "University", "San Diego", "CA", "USA"),
            new Institution("Georgia Institute of Technology", "University", "Atlanta", "GA", "USA"),
            new Institution("University of Texas at Austin", "University", "Austin", "TX", "USA"),
            new Institution("University of Wisconsin-Madison", "University", "Madison", "WI", "USA"),
            new Institution("University of Washington", "University", "Seattle", "WA", "USA"),
            new Institution("Pennsylvania State University", "University", "University Park", "PA", "USA"),
            new Institution("Ohio State University", "University", "Columbus", "OH", "USA"),
            new Institution("University of Florida", "University", "Gainesville", "FL", "USA"),
            new Institution("University of Illinois at Urbana-Champaign", "University", "Urbana", "IL", "USA"),
            new Institution("University of Georgia", "University", "Athens", "GA", "USA"),
            new Institution("Purdue University", "University", "West Lafayette", "IN", "USA")
        };

        for (Institution institution : institutions) {
            institutionRepository.save(institution);
        }
    }

    private void initializeChapters() {
        // Get some institutions for sample chapters
        java.util.List<Institution> institutions = institutionRepository.findAll();
        if (institutions.size() >= 10) {
            for (int i = 0; i < Math.min(20, institutions.size()); i++) {
                Institution institution = institutions.get(i);
                Chapter chapter = new Chapter();
                chapter.setName("Turning Point Chapter " + (i + 1));
                chapter.setUniversityName(institution.getName());
                chapter.setState(institution.getState());
                chapter.setCity(institution.getLocation());
                chapter.setActive(true);
                chapter.setDescription("A vibrant chapter dedicated to creating positive change at " + institution.getName());
                chapterRepository.save(chapter);
            }
        }
    }
}