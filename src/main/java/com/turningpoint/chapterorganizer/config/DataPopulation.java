package com.turningpoint.chapterorganizer.config;

import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.repository.ChapterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataPopulation implements CommandLineRunner {

    @Autowired
    private ChapterRepository chapterRepository;

    @Override
    public void run(String... args) throws Exception {
        // Only populate if no chapters exist
        if (chapterRepository.count() == 0) {
            populateChapters();
        }
    }

    private void populateChapters() {
        List<Chapter> chapters = Arrays.asList(
            new Chapter("Alpha Beta Chapter", "University of California, Los Angeles", "California", "Los Angeles"),
            new Chapter("Gamma Delta Chapter", "Stanford University", "California", "Stanford"),
            new Chapter("Epsilon Zeta Chapter", "University of Southern California", "California", "Los Angeles"),
            new Chapter("Theta Iota Chapter", "University of California, Berkeley", "California", "Berkeley"),
            new Chapter("Kappa Lambda Chapter", "California Institute of Technology", "California", "Pasadena"),
            
            new Chapter("Mu Nu Chapter", "New York University", "New York", "New York"),
            new Chapter("Xi Omicron Chapter", "Columbia University", "New York", "New York"),
            new Chapter("Pi Rho Chapter", "Cornell University", "New York", "Ithaca"),
            new Chapter("Sigma Tau Chapter", "University of Rochester", "New York", "Rochester"),
            new Chapter("Upsilon Phi Chapter", "Syracuse University", "New York", "Syracuse"),
            
            new Chapter("Chi Psi Chapter", "Harvard University", "Massachusetts", "Cambridge"),
            new Chapter("Omega Alpha Chapter", "Massachusetts Institute of Technology", "Massachusetts", "Cambridge"),
            new Chapter("Beta Gamma Chapter", "Boston University", "Massachusetts", "Boston"),
            new Chapter("Delta Epsilon Chapter", "Northeastern University", "Massachusetts", "Boston"),
            new Chapter("Zeta Eta Chapter", "Tufts University", "Massachusetts", "Medford"),
            
            new Chapter("Iota Kappa Chapter", "University of Texas at Austin", "Texas", "Austin"),
            new Chapter("Lambda Mu Chapter", "Texas A&M University", "Texas", "College Station"),
            new Chapter("Nu Xi Chapter", "Rice University", "Texas", "Houston"),
            new Chapter("Omicron Pi Chapter", "University of Houston", "Texas", "Houston"),
            new Chapter("Rho Sigma Chapter", "Texas Tech University", "Texas", "Lubbock"),
            
            new Chapter("Tau Upsilon Chapter", "University of Florida", "Florida", "Gainesville"),
            new Chapter("Phi Chi Chapter", "Florida State University", "Florida", "Tallahassee"),
            new Chapter("Psi Omega Chapter", "University of Miami", "Florida", "Coral Gables"),
            new Chapter("Alpha Alpha Chapter", "Florida Institute of Technology", "Florida", "Melbourne"),
            new Chapter("Beta Beta Chapter", "University of Central Florida", "Florida", "Orlando"),
            
            new Chapter("Gamma Gamma Chapter", "University of Illinois at Urbana-Champaign", "Illinois", "Urbana"),
            new Chapter("Delta Delta Chapter", "Northwestern University", "Illinois", "Evanston"),
            new Chapter("Epsilon Epsilon Chapter", "University of Chicago", "Illinois", "Chicago"),
            new Chapter("Zeta Zeta Chapter", "Illinois Institute of Technology", "Illinois", "Chicago"),
            new Chapter("Eta Eta Chapter", "DePaul University", "Illinois", "Chicago"),
            
            new Chapter("Theta Theta Chapter", "University of Washington", "Washington", "Seattle"),
            new Chapter("Iota Iota Chapter", "Washington State University", "Washington", "Pullman"),
            new Chapter("Kappa Kappa Chapter", "Seattle University", "Washington", "Seattle"),
            new Chapter("Lambda Lambda Chapter", "Gonzaga University", "Washington", "Spokane"),
            new Chapter("Mu Mu Chapter", "Western Washington University", "Washington", "Bellingham"),
            
            new Chapter("Nu Nu Chapter", "University of Michigan", "Michigan", "Ann Arbor"),
            new Chapter("Xi Xi Chapter", "Michigan State University", "Michigan", "East Lansing"),
            new Chapter("Omicron Omicron Chapter", "Wayne State University", "Michigan", "Detroit"),
            new Chapter("Pi Pi Chapter", "Western Michigan University", "Michigan", "Kalamazoo"),
            new Chapter("Rho Rho Chapter", "Central Michigan University", "Michigan", "Mount Pleasant"),
            
            new Chapter("Sigma Sigma Chapter", "University of Georgia", "Georgia", "Athens"),
            new Chapter("Tau Tau Chapter", "Georgia Institute of Technology", "Georgia", "Atlanta"),
            new Chapter("Upsilon Upsilon Chapter", "Emory University", "Georgia", "Atlanta"),
            new Chapter("Phi Phi Chapter", "Georgia State University", "Georgia", "Atlanta"),
            new Chapter("Chi Chi Chapter", "Kennesaw State University", "Georgia", "Kennesaw"),
            
            new Chapter("Psi Psi Chapter", "University of North Carolina at Chapel Hill", "North Carolina", "Chapel Hill"),
            new Chapter("Omega Omega Chapter", "Duke University", "North Carolina", "Durham"),
            new Chapter("Alpha Theta Chapter", "North Carolina State University", "North Carolina", "Raleigh"),
            new Chapter("Beta Zeta Chapter", "Wake Forest University", "North Carolina", "Winston-Salem"),
            new Chapter("Gamma Epsilon Chapter", "University of North Carolina at Charlotte", "North Carolina", "Charlotte")
        );

        chapterRepository.saveAll(chapters);
        System.out.println("Populated database with " + chapters.size() + " chapters!");
    }
}