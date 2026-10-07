package org.xenon.echo.services;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xenon.echo.entities.*;
import org.xenon.echo.enums.Role;
import org.xenon.echo.repositories.*;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class SeedService {
    private final UserRepository userRepository;
    private final NoteRepository noteRepository;
    private final TagRepository tagRepository;
    private final TopicRepository topicRepository;
    private final MemoryItemRepository memoryItemRepository;
    private final PasswordEncoder passwordEncoder;

    private record MemorySeed(String front, String back) {}

    @Transactional
    public void seedData() {
        // Ensure default demo users exist if database is fresh
        User user1 = userRepository.findByEmail("romans@echo.com").orElseGet(() -> {
            User u = new User();
            u.setName("Daniel Romans");
            u.setEmail("romans@echo.com");
            u.setPassword(passwordEncoder.encode("Password123"));
            u.setRole(Role.ADMIN);
            u.setVerified(true);
            return userRepository.save(u);
        });

        User user2 = userRepository.findByEmail("butcher@echo.com").orElseGet(() -> {
            User u = new User();
            u.setName("Billy Butcher");
            u.setEmail("butcher@echo.com");
            u.setPassword(passwordEncoder.encode("Password123"));
            u.setRole(Role.ADMIN);
            u.setVerified(true);
            return userRepository.save(u);
        });

        User user3 = userRepository.findByEmail("wills@echo.com").orElseGet(() -> {
            User u = new User();
            u.setName("David Williams");
            u.setEmail("wills@echo.com");
            u.setPassword(passwordEncoder.encode("Password123"));
            u.setRole(Role.USER);
            u.setVerified(true);
            return userRepository.save(u);
        });

        User user4 = userRepository.findByEmail("hill@echo.com").orElseGet(() -> {
            User u = new User();
            u.setName("Hugh Hill");
            u.setEmail("hill@echo.com");
            u.setPassword(passwordEncoder.encode("Password123"));
            u.setRole(Role.USER);
            u.setVerified(true);
            return userRepository.save(u);
        });

        // Locate or create owner: Blessed Winner
        User blessedWinner = userRepository.findByEmail("blessedwinner66@gmail.com")
                .or(() -> userRepository.findAll().stream().filter(u -> u.getName() != null && u.getName().equalsIgnoreCase("Blessed Winner")).findFirst())
                .orElseGet(() -> {
                    User u = new User();
                    u.setName("Blessed Winner");
                    u.setEmail("blessedwinner66@gmail.com");
                    u.setPassword(passwordEncoder.encode("Password123"));
                    u.setRole(Role.USER);
                    u.setVerified(true);
                    return userRepository.save(u);
                });

        seedLibraryForUser(blessedWinner);
    }

    private void seedLibraryForUser(User owner) {
        // Topic 1: Scripture & Theology
        Topic scriptureTopic = getOrCreateTopic(owner, "Scripture & Theology", "Biblical themes, historical context, covenantal theology, and hermeneutics.");
        seedNote(scriptureTopic, "The Covenantal Themes of Divine Justice and Mercy",
                "Classical biblical theology explores how divine justice and mercy intersect throughout scriptural narratives. From the Abrahamic covenant to the prophetic traditions of Isaiah and Amos, righteousness is defined not merely as legal adherence but as relational fidelity. The tension between divine holiness and human frailty creates a framework where justice demands accountability while grace provides redemption.",
                List.of(
                        new MemorySeed("How is righteousness defined in biblical covenantal theology?", "Righteousness is defined as relational fidelity and covenantal loyalty rather than mere legalism."),
                        new MemorySeed("Which Old Testament prophets prominently emphasize divine justice alongside mercy?", "Prophets such as Isaiah and Amos.")
                ), owner);

        seedNote(scriptureTopic, "Early Church History in Roman North Africa",
                "During the 2nd to 5th centuries, Roman North Africa—particularly Carthage and Hippo—served as a crucial cradle of Christian theological development. Figures like Tertullian, Cyprian, and Augustine of Hippo shaped Latin theological vocabulary. Tertullian coined fundamental Latin terminology for the Trinity, while Augustine's writings on grace, original sin, and the City of God profoundly influenced Western civilization.",
                List.of(
                        new MemorySeed("Which early church father in Roman North Africa coined Latin terms for the Trinity?", "Tertullian."),
                        new MemorySeed("What major theological concepts did Augustine of Hippo develop?", "Original sin, divine grace, and the two cities framework in 'The City of God'.")
                ), owner);

        seedNote(scriptureTopic, "Grammatical-Historical Hermeneutics and Exegesis",
                "Effective biblical interpretation relies on grammatical-historical exegesis. This approach seeks the original authorial intent by examining the historical context, cultural background, and linguistic nuances of the ancient text. Disciplined reasoning prevents subjective cognitive bias, ensuring that the interpreter does not read modern cultural assumptions back into ancient scriptures.",
                List.of(
                        new MemorySeed("What is the main objective of grammatical-historical hermeneutics?", "To discover original authorial intent through historical context and linguistic analysis."),
                        new MemorySeed("Why is disciplined reasoning critical in biblical exegesis?", "To prevent cognitive bias and avoid reading modern cultural assumptions into ancient texts.")
                ), owner);

        seedNote(scriptureTopic, "Oral Tradition and the Preservation of Sacred Memory",
                "Before the formal compilation of scribal manuscripts, ancient Israelite and early Christian communities relied on structured oral tradition to preserve sacred memory. Mnemonic structures, poetic parallelism, and ritual recitations allowed complex historical and theological narratives to pass through generations with extraordinary verbal fidelity.",
                List.of(
                        new MemorySeed("What techniques did ancient oral traditions use to preserve sacred memory?", "Mnemonic structures, poetic parallelism, and ritual recitations.")
                ), owner);

        // Topic 2: Geography & Culture
        Topic geoTopic = getOrCreateTopic(owner, "Geography & Culture", "Physical topography, human geography, regional traditions, and cultural heritage.");
        seedNote(geoTopic, "The Rift Valley and Tectonic Landscapes of Africa",
                "The Great Rift Valley spans thousands of kilometers across East Africa, created by the diverging African and Somali tectonic plates. This unique topography features deep rift lakes such as Lake Tanganyika, volcanic peaks like Kilimanjaro, and fertile valleys that served as the cradle of early hominin evolution and human migration.",
                List.of(
                        new MemorySeed("What tectonic mechanism formed the Great Rift Valley in Africa?", "The divergence of the African and Somali tectonic plates."),
                        new MemorySeed("Name two significant geographic features located in the East African Rift System.", "Lake Tanganyika and Mount Kilimanjaro.")
                ), owner);

        seedNote(geoTopic, "Roman Infrastructure and Mediterranean Maritime Trade",
                "The expansion of the Roman state was fueled by an unprecedented network of paved roads and maritime trade routes spanning the Mediterranean. Ports across North Africa exported grain, olive oil, and garum to Rome, while Roman aqueducts and engineering marvels facilitated rapid urbanization and cultural assimilation across three continents.",
                List.of(
                        new MemorySeed("Which primary agricultural goods were exported from Roman North Africa to Rome?", "Grain and olive oil."),
                        new MemorySeed("How did Roman engineering support urbanization across conquered provinces?", "Through aqueducts, paved road networks, and maritime port infrastructure.")
                ), owner);

        seedNote(geoTopic, "Cultural Memory in Indigenous Oral Storytelling",
                "In non-literate and traditional societies, cultural memory is preserved through oral storytelling, music, and dance. Griots in West Africa function as living archives, memorizing genealogies, historical battles, and moral proverbs. This dynamic oral preservation connects past ancestral wisdom directly to contemporary community identity.",
                List.of(
                        new MemorySeed("What is the historical role of a Griot in West African culture?", "A living archive who preserves oral history, genealogies, and cultural proverbs.")
                ), owner);

        seedNote(geoTopic, "Urban Geography and Smart City Sensor Technology",
                "Modern urban geography explores how IoT sensor technology, spatial data analytics, and intelligent transit systems optimize urban living. By monitoring traffic patterns, energy consumption, and environmental quality in real time, smart city technology aims to make urban centers more sustainable and resilient.",
                List.of(
                        new MemorySeed("How does IoT sensor technology transform modern urban geography?", "By enabling real-time monitoring of traffic, energy usage, and urban environmental metrics.")
                ), owner);

        // Topic 3: History
        Topic historyTopic = getOrCreateTopic(owner, "History", "Historical eras, civilizations, political movements, and historical patterns.");
        seedNote(historyTopic, "The Crisis of the Third Century and the Late Roman Empire",
                "The Third Century Crisis (235–284 AD) brought the Roman Empire to the brink of collapse due to hyperinflation, civil wars, military anarchy, and foreign invasions. Diocletian restored order by establishing the Tetrarchy and reorganizing Roman administrative provinces, setting the stage for the Byzantine era.",
                List.of(
                        new MemorySeed("What primary factors caused the Roman Crisis of the Third Century?", "Hyperinflation, civil wars among army generals, and external barbarian invasions."),
                        new MemorySeed("How did Emperor Diocletian restructure Roman imperial governance?", "By creating the Tetrarchy system, dividing power among four co-rulers.")
                ), owner);

        seedNote(historyTopic, "Decolonization Movements across 20th Century Africa",
                "Following World War II, decolonization swept across Africa as independence movements challenged European imperial rule. Leaders like Kwame Nkrumah in Ghana and Jomo Kenyatta in Kenya advocated for Pan-African solidarity, leading to national sovereignty and the formation of the Organization of African Unity in 1963.",
                List.of(
                        new MemorySeed("Which nation became the first sub-Saharan African country to gain independence in 1957?", "Ghana, under the leadership of Kwame Nkrumah."),
                        new MemorySeed("What continental organization was established in 1963 to promote Pan-African unity?", "The Organization of African Unity (OAU).")
                ), owner);

        seedNote(historyTopic, "Historiography and the Politics of Public Memory",
                "Historiography examines how historical narratives are constructed and revised over time. Public memory is often shaped by political agendas, memorial monuments, and national curricula. Re-evaluating historical evidence allows societies to uncover forgotten perspectives and dismantle biased historical myths.",
                List.of(
                        new MemorySeed("What is the core distinction between history and historiography?", "History is the study of past events; historiography is the study of how historical narratives are written and interpreted.")
                ), owner);

        seedNote(historyTopic, "The Industrial Revolution and Steam Power Technology",
                "The advent of the Watt steam engine in the late 18th century revolutionized manufacturing, mining, and transportation. Steam technology powered textile mills and locomotives, accelerating global trade while fundamentally altering labor conditions, urban demographics, and economic organization.",
                List.of(
                        new MemorySeed("Who developed the refined steam engine that powered the Industrial Revolution?", "James Watt.")
                ), owner);

        // Topic 4: Philosophy & Critical Thinking
        Topic philTopic = getOrCreateTopic(owner, "Philosophy & Critical Thinking", "Logic, formal reasoning, ethics, epistemology, and sound argument analysis.");
        seedNote(philTopic, "Principles of Sound Reasoning and Fallacy Detection",
                "Logical reasoning is split into deductive and inductive frameworks. Deductive reasoning guarantees the truth of its conclusion if the premises are true (e.g., syllogisms). Inductive reasoning derives probabilistic generalizations from specific empirical observations, forming the bedrock of scientific inquiry.",
                List.of(
                        new MemorySeed("What is the key difference between deductive and inductive reasoning?", "Deductive reasoning guarantees conclusions from true premises; inductive reasoning provides probabilistic general conclusions."),
                        new MemorySeed("What makes a deductive argument valid and sound?", "Validity means logical structure holds; soundness means valid structure PLUS true premises.")
                ), owner);

        seedNote(philTopic, "Distributive Justice and the Social Contract",
                "John Rawls proposed a theory of justice based on the 'veil of ignorance', arguing that fair social rules are chosen when no one knows their personal wealth or status. Robert Nozick countered with an entitlement theory of justice, arguing that any distribution is just if acquired through legitimate initial acquisition and voluntary trade.",
                List.of(
                        new MemorySeed("What is John Rawls' 'veil of ignorance' thought experiment?", "A hypothetical scenario where principles of justice are chosen without knowing one's social status, talents, or wealth."),
                        new MemorySeed("How does Robert Nozick define economic justice?", "As legitimate initial acquisition and voluntary transfer of private property.")
                ), owner);

        seedNote(philTopic, "Epistemology and Confirmation Bias in Belief Formation",
                "Epistemology studies the nature, origin, and limits of human knowledge. A key barrier to true belief is confirmation bias—the tendency to favor information that confirms pre-existing beliefs while ignoring counter-evidence. Active open-mindedness and rigorous hypothesis testing are necessary to overcome epistemic bias.",
                List.of(
                        new MemorySeed("Define confirmation bias in epistemology.", "The tendency to search for, interpret, and recall information in a way that confirms prior beliefs.")
                ), owner);

        seedNote(philTopic, "Philosophy of Mind and Artificial Consciousness",
                "The philosophy of mind questions whether consciousness can emerge from synthetic silicon technology. Functionalism suggests mental states are defined by their causal roles, whereas Searle's Chinese Room argument contends that processing symbols according to syntax does not equate to genuine semantic understanding or subjective awareness.",
                List.of(
                        new MemorySeed("What does John Searle's Chinese Room thought experiment demonstrate?", "That syntactic symbol manipulation by a computer does not constitute genuine semantic understanding.")
                ), owner);

        // Topic 5: Psychology & Human Behavior
        Topic psychTopic = getOrCreateTopic(owner, "Psychology & Human Behavior", "Cognitive psychology, neuroscience, behavioral economics, and human motivation.");
        seedNote(psychTopic, "Understanding Cognitive Biases in Decision Making",
                "Human judgment is systematically skewed by cognitive biases. The availability heuristic leads people to overestimate the likelihood of memorable events. Anchoring bias causes over-reliance on the first piece of information received. Recognizing these mental shortcuts is vital for rational decision-making.",
                List.of(
                        new MemorySeed("What is the availability heuristic in cognitive psychology?", "Estimating the probability of an event based on how easily examples come to mind."),
                        new MemorySeed("Explain anchoring bias.", "The cognitive tendency to rely too heavily on the first piece of information encountered.")
                ), owner);

        seedNote(psychTopic, "Neurobiology of Long-Term Memory Storage",
                "Memory consolidation transforms fragile short-term neural activity into permanent long-term storage. The hippocampus coordinates this process during sleep, strengthening synaptic connections through long-term potentiation (LTP) and gradually transferring structural memory traces to the cerebral cortex.",
                List.of(
                        new MemorySeed("Which brain structure is primary for consolidating new long-term declarative memory?", "The hippocampus."),
                        new MemorySeed("What cellular process forms the biological basis for long-term memory strengthening?", "Long-Term Potentiation (LTP).")
                ), owner);

        seedNote(psychTopic, "Cognitive Principles of Effective Learning",
                "Educational psychology highlights spaced practice and active recall as the most effective strategies for long-term learning. Passive re-reading creates an illusion of competence, whereas testing oneself forces memory retrieval, creating stronger neural pathways and enduring retention.",
                List.of(
                        new MemorySeed("Why is active recall superior to passive re-reading for long-term learning?", "Active recall forces memory retrieval, strengthening neural pathways and long-term retention.")
                ), owner);

        seedNote(psychTopic, "Behavioral Economics and Moral Reasoning",
                "Daniel Kahneman's System 1 and System 2 cognitive frameworks reveal that moral reasoning is often an afterthought. System 1 generates fast, emotional moral intuitions, while System 2 engages in slow, deliberate logical reasoning to justify initial gut reactions post hoc.",
                List.of(
                        new MemorySeed("Distinguish System 1 from System 2 processing in Kahneman's model.", "System 1 is fast, automatic, and emotional; System 2 is slow, deliberate, and analytical.")
                ), owner);

        // Topic 6: Science & Technology
        Topic sciTopic = getOrCreateTopic(owner, "Science & Technology", "Computer science, artificial intelligence, physical sciences, and modern engineering.");
        seedNote(sciTopic, "Evolution of Microprocessor Architecture and Computing Technology",
                "Modern computing technology evolved from Von Neumann architectures using separate memory and processing units to multi-core parallel processing systems. Advanced semiconductor lithography allows billions of nanometer-scale transistors to operate on a single silicon microchip, powering global digital infrastructure.",
                List.of(
                        new MemorySeed("What are the core components of the Von Neumann computing architecture?", "A central processing unit (CPU), memory, storage, and input/output mechanisms.")
                ), owner);

        seedNote(sciTopic, "Machine Learning Paradigms and Neural Networks",
                "Machine learning enables computers to learn patterns from data without explicit programming. Supervised learning utilizes labeled datasets, unsupervised learning discovers hidden structures, and reinforcement learning optimizes actions via reward signals. Deep neural network technology drives modern artificial intelligence breakthroughs.",
                List.of(
                        new MemorySeed("Identify the three main paradigms of machine learning?", "Supervised learning, unsupervised learning, and reinforcement learning."),
                        new MemorySeed("What algorithm is used to train multi-layer artificial neural networks?", "Backpropagation.")
                ), owner);

        seedNote(sciTopic, "Working Memory Limits in Software Technology UX",
                "Software developers and UI designers must design around human working memory limits. Miller's Law suggests that individuals can hold roughly 7 ± 2 items in working memory simultaneously. Modern user interface technology minimizes cognitive load by chunking complex information into digestible visual components.",
                List.of(
                        new MemorySeed("What is Miller's Law regarding human working memory capacity?", "The average human can hold 7 ± 2 items in working memory at any given time.")
                ), owner);

        seedNote(sciTopic, "Algorithmic Bias and Social Justice in Technology",
                "As machine learning models automate decisions in hiring, healthcare, and criminal justice, algorithmic bias has emerged as a major challenge. When training datasets reflect historical human bias, technology inadvertently perpetuates systemic unfairness, necessitating rigorous fairness auditing and ethical AI development.",
                List.of(
                        new MemorySeed("How does algorithmic bias manifest in artificial intelligence technology?", "When models trained on historical data reproduce and amplify human societal biases.")
                ), owner);

        // Topic 7: Literature & Ideas
        Topic litTopic = getOrCreateTopic(owner, "Literature & Ideas", "Classics, literary theory, narrative structure, and foundational human ideas.");
        seedNote(litTopic, "The Concept of Justice in Greek Tragedy",
                "Classical Greek tragedy interrogates the nature of justice, cosmic order, and fate. In Aeschylus' Oresteia, the primitive cycle of blood revenge gives way to legal trial by jury, marking a transition from tribal retribution to institutional civic justice in ancient democratic Athens.",
                List.of(
                        new MemorySeed("What social evolution is dramatized in Aeschylus' Oresteia trilogy?", "The transition from ancestral blood-feud retribution to institutional trial by jury.")
                ), owner);

        seedNote(litTopic, "Virgil's Aeneid and Roman Imperial Ideology",
                "Virgil composed the Aeneid during the reign of Augustus, framing the mythic origin of Rome through the Trojan hero Aeneas. The poem explores the Roman ideal of 'pietas' (duty to gods, family, and state) and struggles with the tragic human cost of imperial glory.",
                List.of(
                        new MemorySeed("What Roman virtue is embodied by Aeneas in Virgil's epic poem?", "Pietas (devotion and duty to divine mandate, family, and country).")
                ), owner);

        seedNote(litTopic, "Post-Colonial African Literature and Cultural Identity",
                "Post-colonial African literature reclaims narrative agency from colonial historiography. Works like Chinua Achebe's 'Things Fall Apart' provide a nuanced insider perspective on Igbo society before and during British colonial encroachment, highlighting how cultural identity resists external domination.",
                List.of(
                        new MemorySeed("What central theme does Chinua Achebe explore in 'Things Fall Apart'?", "The impact of European colonialism on traditional Igbo society and cultural integrity.")
                ), owner);

        seedNote(litTopic, "Narrative Structure and Experiential Learning",
                "Literature is a cognitive simulator. According to narrative theory, storytelling engages the human brain's natural learning mechanisms by allowing readers to simulate complex social scenarios, practice emotional empathy, and internalize abstract philosophical ideas through concrete character arcs.",
                List.of(
                        new MemorySeed("Why is storytelling considered a cognitive simulator for human learning?", "It enables listeners/readers to simulate complex social scenarios and internalize abstract ideas through narrative experience.")
                ), owner);

        // Topic 8: Personal Growth & Reflection
        Topic growthTopic = getOrCreateTopic(owner, "Personal Growth & Reflection", "Self-improvement, daily habits, lifelong learning, and intentional living.");
        seedNote(growthTopic, "Architecting a System for Lifelong Learning",
                "Effective personal growth requires a deliberate system for continuous learning. Combining active reading with digital note-taking, periodic reflection, and output creation prevents passive consumption and converts fleeting ideas into an organized personal knowledge base.",
                List.of(
                        new MemorySeed("What are the core components of a continuous personal learning system?", "Active reading, structured digital note-taking, periodic reflection, and creative output.")
                ), owner);

        seedNote(growthTopic, "Overcoming Cognitive Bias in Personal Reflection",
                "Self-reflection can be corrupted by self-serving bias and rationalization. To foster genuine self-growth, individuals must actively question their motives, seek honest external feedback, and conduct periodic audits to uncover unconscious personal bias.",
                List.of(
                        new MemorySeed("How does self-serving bias hinder personal growth during reflection?", "It causes people to attribute successes to internal skill and failures to external factors, distorting self-awareness.")
                ), owner);

        seedNote(growthTopic, "Leveraging Spaced Repetition for Permanent Memory",
                "Hermann Ebbinghaus discovered that human memory decays exponentially over time without review. Spaced repetition technology combats this forgetting curve by scheduling memory reviews at expanding intervals, maximizing long-term knowledge retention with minimal study effort.",
                List.of(
                        new MemorySeed("What phenomenon does spaced repetition technology combat?", "The Ebbinghaus exponential forgetting curve."),
                        new MemorySeed("How do expanding review intervals affect memory retention?", "They reinforce memory consolidation just before retrieval failure occurs, locking knowledge into long-term memory.")
                ), owner);

        seedNote(growthTopic, "Digital Hygiene and Focused Technology Use",
                "Maintaining high cognitive performance requires intentional boundaries around personal technology. Practicing digital hygiene—such as turning off non-essential notifications and designating deep work blocks—prevents attention fragmentation and protects mental clarity.",
                List.of(
                        new MemorySeed("What is the primary benefit of practicing intentional digital hygiene?", "Protecting mental focus from attention fragmentation and reducing cognitive fatigue.")
                ), owner);
    }

    private Topic getOrCreateTopic(User owner, String name, String description) {
        return topicRepository.findByNameIgnoreCaseAndUserId(name, owner.getId())
                .orElseGet(() -> {
                    Topic topic = new Topic();
                    topic.setName(name);
                    topic.setDescription(description);
                    topic.setUser(owner);
                    topic.setCreatedAt(LocalDateTime.now());
                    return topicRepository.save(topic);
                });
    }

    private Note seedNote(Topic topic, String title, String content, List<MemorySeed> memorySeeds, User owner) {
        Note note = noteRepository.findByTitleIgnoreCaseAndTopicId(title, topic.getId())
                .orElseGet(() -> {
                    Note n = new Note();
                    n.setTitle(title);
                    n.setContent(content);
                    n.setTopic(topic);
                    n.setCreatedAt(LocalDateTime.now());
                    return noteRepository.save(n);
                });

        for (MemorySeed ms : memorySeeds) {
            if (!memoryItemRepository.existsByFrontIgnoreCaseAndNoteId(ms.front(), note.getId())) {
                MemoryItem item = new MemoryItem();
                item.setFront(ms.front());
                item.setBack(ms.back());
                item.setSource(title);
                item.setUser(owner);
                item.setNote(note);
                item.setInterval(1);
                item.setEaseFactor(2.5f);
                item.setReviewCount(0);
                item.setNextReviewDate(LocalDateTime.now());
                item.setCreatedAt(LocalDateTime.now());
                memoryItemRepository.save(item);
            }
        }
        return note;
    }
}
