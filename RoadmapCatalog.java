import java.util.List;

/** Skill-specific learning stages, practice guidance, and resource links. */
final class RoadmapCatalog {
    private RoadmapCatalog() { }

    static List<String> stages(String s){
        return switch(s){
            case "Java" -> List.of("Fundamentals: types, operators, decisions, loops, arrays, methods","OOP: classes, constructors, encapsulation, inheritance, interfaces","Core Java: exceptions, collections, generics, files, streams","Data/backend: SQL, JDBC, prepared statements, REST concepts","Projects/interviews: build and test an app, then practice DSA and Java questions");
            case "Python" -> List.of("Syntax: variables, collections, conditions, loops, functions","Programs: modules, exceptions, files, comprehensions","OOP and virtual environments","APIs and data: JSON, HTTP, CSV, testing","Build a CLI or data analysis project and explain its design");
            case "Object-Oriented Programming" -> List.of("Classes, objects, state, behavior, constructors","Encapsulation, access control, invariants","Inheritance, composition, polymorphism","Abstraction, interfaces, dependency inversion","Model a domain and refactor using SOLID principles");
            case "Spring Boot" -> List.of("Prerequisites: Java OOP, collections, HTTP, REST, dependency injection","Project structure, configuration, controllers, validation","Services, repositories, Spring Data JPA","Error handling, authentication basics, automated tests","Build and document a REST API with persistence");
            case "Software Development" -> List.of("Requirements, user stories, acceptance criteria","Git workflow, branches, commits, code review","Design, implementation, debugging, refactoring","Unit tests, integration tests, release checks","Plan, build, document, demo an end-to-end application");
            case "Data Structures and Algorithms" -> List.of("Big-O, arrays, strings, search, sorting","Linked lists, stacks, queues, hash maps, sets","Trees, heaps, graph traversal, recursion","Two pointers, sliding window, backtracking, dynamic programming","Solve timed problems and explain correctness and complexity");
            case "Problem Solving" -> List.of("Restate the problem, inputs, outputs, constraints","Break work into cases and examples","Choose an approach and compare alternatives","Implement, test edge cases, debug systematically","Explain the result and review possible improvements");
            case "Database Management Systems" -> List.of("Tables, types, primary keys, relationships","SELECT, filters, sorting, joins, aggregation","Normalization, constraints, indexes","Transactions, CRUD, prepared statements","Design a schema and connect it to a Java application");
            case "Data Analysis" -> List.of("Ask a question and identify useful data","Clean data, missing values, validate assumptions","Summarize with statistics and comparisons","Interpret findings and communicate uncertainty","Create a small analysis report with reproducible steps");
            case "Web Development" -> List.of("HTML semantics and accessible page structure","CSS layout and responsive design","JavaScript, events, forms, validation","HTTP, APIs, JSON, browser debugging","Build and test a responsive portfolio project");
            case "Git and Version Control" -> List.of("Repository, working tree, staging, commits","Branches, merges, conflict resolution, pull requests","Ignore files, tags, remotes, recovery","Focused commits and diff review","Collaborate on a feature branch and document the workflow");
            case "Communication" -> List.of("Audience, purpose, key message","Organize ideas into a clear explanation","Active listening, questions, concise updates","Deliver a short presentation and handle feedback","Reflect and repeat with improvements");
            case "Teamwork" -> List.of("Agree on shared goals and roles","Active listening and constructive feedback","Coordinate tasks and communicate blockers","Resolve disagreements with evidence and priorities","Complete a group activity and reflect on collaboration");
            case "Time Management" -> List.of("List tasks and estimate effort","Prioritize impact, urgency, dependencies","Break work into focused sessions and milestones","Track interruptions and adjust the plan","Review the week and choose one improvement");
            case "Critical Thinking" -> List.of("Separate claims, evidence, and assumptions","Check source quality and missing information","Compare explanations and alternative hypotheses","Make a reasoned evidence-based decision","Explain limits and uncertainty in a short analysis");
            case "Leadership" -> List.of("Set a clear outcome and explain its purpose","Delegate with ownership and check-ins","Listen and give decision context","Give useful feedback and remove blockers","Lead a project review and capture lessons");
            case "Presentation Skills" -> List.of("Define audience and one takeaway","Structure opening, evidence, conclusion","Design readable slides and visuals","Rehearse timing, voice, transitions","Deliver a short talk and apply feedback");
            case "Adaptability" -> List.of("Notice changes in requirements or context","Identify knowns, unknowns, and urgency","Try a small experiment and evaluate it","Adjust plans and communicate trade-offs","Reflect on a change scenario and record lessons");
            default -> List.of("Learn key concepts and vocabulary","Work through a guided example","Practice independently and review mistakes","Apply the skill in a small project","Demonstrate learning and reassess");
        };
    }
    static String practice(String s){
        if(s.equals("Java")||s.equals("Python")||s.equals("Spring Boot")||s.equals("Web Development"))return "Implement a small feature, handle an edge case, and test it.";
        if(s.equals("Communication")||s.equals("Presentation Skills"))return "Prepare a short talk, deliver it, and reflect on feedback.";
        if(s.equals("Teamwork")||s.equals("Leadership"))return "Complete a group task with roles, check-ins, and a retrospective.";
        return "Complete three practice exercises, review errors, and explain your approach.";
    }

    static String resource(String n){return switch(n){case "Java"->"dev.java/learn";case "Python"->"docs.python.org/3/tutorial";case "Database Management Systems"->"dev.mysql.com/doc";case "Web Development"->"developer.mozilla.org/en-US/docs/Learn";case "Git and Version Control"->"git-scm.com/book";default->"OpenLearn — introductory learning materials";};}
}
