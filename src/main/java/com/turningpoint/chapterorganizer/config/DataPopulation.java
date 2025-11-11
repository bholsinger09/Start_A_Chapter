package com.turningpoint.chapterorganizer.config;

import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.entity.MemberRole;
import com.turningpoint.chapterorganizer.repository.ChapterRepository;
import com.turningpoint.chapterorganizer.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

@Component
public class DataPopulation implements CommandLineRunner {

    @Autowired
    private ChapterRepository chapterRepository;
    
    @Autowired
    private MemberRepository memberRepository;

    @Override
    public void run(String... args) throws Exception {
        // Only populate if no chapters exist
        if (chapterRepository.count() == 0) {
            populateChapters();
        }
        
        // Only populate members if none exist
        if (memberRepository.count() == 0) {
            populateMembers();
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
            new Chapter("Gamma Epsilon Chapter", "University of North Carolina at Charlotte", "North Carolina", "Charlotte"),
            
            // Add Boise State chapter
            new Chapter("Delta Kappa Chapter", "Boise State University", "Idaho", "Boise")
        );

        chapterRepository.saveAll(chapters);
        System.out.println("Populated database with " + chapters.size() + " chapters!");
    }
    
    private void populateMembers() {
        // Get some chapters to assign members to
        List<Chapter> chapters = chapterRepository.findAll();
        if (chapters.isEmpty()) {
            return; // No chapters to assign members to
        }
        
        // Create members manually with setters to avoid anonymous class issues
        List<Member> members = new ArrayList<>();
        
        // Add you as administrator member
        Member benHolsinger = new Member("Ben", "Holsinger", "bholsinger@hotmail.com", chapters.get(0));
        benHolsinger.setUsername("bholsinger");
        benHolsinger.setPhoneNumber("2082841929");
        benHolsinger.setRole(MemberRole.PRESIDENT);
        benHolsinger.setMajor("Computer Science");
        benHolsinger.setGraduationYear("2024");
        benHolsinger.setPassword("Password123");
        members.add(benHolsinger);
        
        // UCLA Chapter members
        Member johnSmith = new Member("John", "Smith", "john.smith@ucla.edu", chapters.get(0));
        johnSmith.setUsername("johnsmith");
        johnSmith.setPhoneNumber("310-555-0101");
        johnSmith.setRole(MemberRole.PRESIDENT);
        johnSmith.setMajor("Computer Science");
        johnSmith.setGraduationYear("2024");
        johnSmith.setPassword("password123");
        members.add(johnSmith);
        
        Member sarahJohnson = new Member("Sarah", "Johnson", "sarah.johnson@ucla.edu", chapters.get(0));
        sarahJohnson.setUsername("sarahj");
        sarahJohnson.setPhoneNumber("310-555-0102");
        sarahJohnson.setRole(MemberRole.VICE_PRESIDENT);
        sarahJohnson.setMajor("Business Administration");
        sarahJohnson.setGraduationYear("2025");
        sarahJohnson.setPassword("password123");
        members.add(sarahJohnson);
        
        Member mikeDavis = new Member("Mike", "Davis", "mike.davis@ucla.edu", chapters.get(0));
        mikeDavis.setUsername("mikedavis");
        mikeDavis.setPhoneNumber("310-555-0103");
        mikeDavis.setRole(MemberRole.TREASURER);
        mikeDavis.setMajor("Economics");
        mikeDavis.setGraduationYear("2024");
        mikeDavis.setPassword("password123");
        members.add(mikeDavis);
        
        // Stanford Chapter members (if available)
        if (chapters.size() > 1) {
            Member emilyWilson = new Member("Emily", "Wilson", "emily.wilson@stanford.edu", chapters.get(1));
            emilyWilson.setUsername("emilyw");
            emilyWilson.setPhoneNumber("650-555-0201");
            emilyWilson.setRole(MemberRole.PRESIDENT);
            emilyWilson.setMajor("Engineering");
            emilyWilson.setGraduationYear("2024");
            emilyWilson.setPassword("password123");
            members.add(emilyWilson);
            
            Member davidBrown = new Member("David", "Brown", "david.brown@stanford.edu", chapters.get(1));
            davidBrown.setUsername("davidb");
            davidBrown.setPhoneNumber("650-555-0202");
            davidBrown.setRole(MemberRole.SECRETARY);
            davidBrown.setMajor("Psychology");
            davidBrown.setGraduationYear("2025");
            davidBrown.setPassword("password123");
            members.add(davidBrown);
        }
        
        // USC Chapter members (if available)
        if (chapters.size() > 2) {
            Member jessicaMartinez = new Member("Jessica", "Martinez", "jessica.martinez@usc.edu", chapters.get(2));
            jessicaMartinez.setUsername("jessicam");
            jessicaMartinez.setPhoneNumber("213-555-0301");
            jessicaMartinez.setRole(MemberRole.PRESIDENT);
            jessicaMartinez.setMajor("Film Studies");
            jessicaMartinez.setGraduationYear("2024");
            jessicaMartinez.setPassword("password123");
            members.add(jessicaMartinez);
            
            Member alexGarcia = new Member("Alex", "Garcia", "alex.garcia@usc.edu", chapters.get(2));
            alexGarcia.setUsername("alexg");
            alexGarcia.setPhoneNumber("213-555-0302");
            alexGarcia.setRole(MemberRole.MEMBER);
            alexGarcia.setMajor("International Relations");
            alexGarcia.setGraduationYear("2026");
            alexGarcia.setPassword("password123");
            members.add(alexGarcia);
        }
        
        // NYU Chapter members (if available)
        if (chapters.size() > 5) {
            Member lisaAnderson = new Member("Lisa", "Anderson", "lisa.anderson@nyu.edu", chapters.get(5));
            lisaAnderson.setUsername("lisaa");
            lisaAnderson.setPhoneNumber("212-555-0501");
            lisaAnderson.setRole(MemberRole.PRESIDENT);
            lisaAnderson.setMajor("Art History");
            lisaAnderson.setGraduationYear("2024");
            lisaAnderson.setPassword("password123");
            members.add(lisaAnderson);
            
            Member ryanThompson = new Member("Ryan", "Thompson", "ryan.thompson@nyu.edu", chapters.get(5));
            ryanThompson.setUsername("ryant");
            ryanThompson.setPhoneNumber("212-555-0502");
            ryanThompson.setRole(MemberRole.VICE_PRESIDENT);
            ryanThompson.setMajor("Finance");
            ryanThompson.setGraduationYear("2025");
            ryanThompson.setPassword("password123");
            members.add(ryanThompson);
        }
        
        // Add a few more sample members to demonstrate the system
        if (chapters.size() > 0) {
            // Add Boise State University members (assuming we have chapters)
            Member caseyPeterson = new Member("Casey", "Peterson", "casey.peterson@boisestate.edu", chapters.get(chapters.size() - 1));
            caseyPeterson.setUsername("caseyp");
            caseyPeterson.setPhoneNumber("208-555-0901");
            caseyPeterson.setRole(MemberRole.PRESIDENT);
            caseyPeterson.setMajor("Business");
            caseyPeterson.setGraduationYear("2024");
            caseyPeterson.setPassword("password123");
            members.add(caseyPeterson);
            
            Member morganJohnson = new Member("Morgan", "Johnson", "morgan.johnson@boisestate.edu", chapters.get(chapters.size() - 1));
            morganJohnson.setUsername("morganj");
            morganJohnson.setPhoneNumber("208-555-0902");
            morganJohnson.setRole(MemberRole.VICE_PRESIDENT);
            morganJohnson.setMajor("Engineering");
            morganJohnson.setGraduationYear("2025");
            morganJohnson.setPassword("password123");
            members.add(morganJohnson);
        }

        memberRepository.saveAll(members);
        System.out.println("Populated database with " + members.size() + " sample members!");
    }
}