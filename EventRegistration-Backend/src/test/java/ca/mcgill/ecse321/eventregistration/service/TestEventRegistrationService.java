package ca.mcgill.ecse321.eventregistration.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import ca.mcgill.ecse321.eventregistration.dao.EventRepository;
import ca.mcgill.ecse321.eventregistration.dao.PersonRepository;
import ca.mcgill.ecse321.eventregistration.dao.RegistrationRepository;
import ca.mcgill.ecse321.eventregistration.model.Event;
import ca.mcgill.ecse321.eventregistration.model.Person;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestEventRegistrationService {
	
	@Autowired
	private EventRegistrationService service;

	@Autowired
	private PersonRepository personRepository;
	@Autowired
	private EventRepository eventRepository;
	@Autowired
	private RegistrationRepository registrationRepository;
	
	@After
	public void clearDatabase() {
		// Fisrt, we clear registrations to avoid exceptions due to inconsistencies
		registrationRepository.deleteAll();
		// Then we can clear the other tables
		personRepository.deleteAll();
		eventRepository.deleteAll();
	}
	
	@Test
	public void testCreatePerson() {
		assertEquals(0, service.getAllPersons().size());

		String name = "Oscar";

		try {
			service.createPerson(name);
		} catch (IllegalArgumentException e) {
			// Check that no error occurred
			fail();
		}

		List<Person> allPersons = service.getAllPersons();

		assertEquals(1, allPersons.size());
		assertEquals(name, allPersons.get(0).getName());
	}
	
	@Test
	public void testCreatePersonNull() {
		assertEquals(0, service.getAllPersons().size());

		String name = null;
		String error = null;

		try {
			service.createPerson(name);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}

		// check error
		assertEquals("Person name cannot be empty!", error);

		// check no change in memory
		assertEquals(0, service.getAllPersons().size());

	}
	
	@Test
	public void testRegisterPersonAndEventDoNotExist() {
		assertEquals(0, service.getAllRegistrations().size());

		String nameP = "Oscar";
		Person person = new Person();
		person.setName(nameP);
		assertEquals(0, service.getAllPersons().size());

		String nameE = "Soccer Game";
		Calendar c = Calendar.getInstance();
		c.set(2016, Calendar.OCTOBER, 16, 9, 00, 0);
		Date eventDate = new Date(c.getTimeInMillis());
		Time startTime = new Time(c.getTimeInMillis());
		c.set(2016, Calendar.OCTOBER, 16, 10, 30, 0);
		Time endTime = new Time(c.getTimeInMillis());
		Event event = new Event();
		event.setName(nameE);
		event.setEventDate(eventDate);
		event.setStartTime(startTime);
		event.setEndTime(endTime);
		assertEquals(0, service.getAllEvents().size());

		String error = null;
		try {
			service.register(person, event);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}

		// check error
		assertEquals("Person does not exist! Event does not exist!", error);

		// check model in memory
		assertEquals(0, service.getAllRegistrations().size());
		assertEquals(0, service.getAllPersons().size());
		assertEquals(0, service.getAllEvents().size());

	}
	
	
}
