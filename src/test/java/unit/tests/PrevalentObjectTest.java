package unit.tests;

import static org.junit.Assert.assertEquals;
import helpers.Customer;
import helpers.Person;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import br.bodoque.CannotRemovePrevalentObjectException;
import br.bodoque.CommandLogList;
import br.bodoque.Filter;
import br.bodoque.Finder;
import br.bodoque.Repository;

public class PrevalentObjectTest extends UnitTestCase {

	@Override
	@Before
	public void setUp() {
		super.setUp();
	}
	
	@Test
	public void shouldCreateASerializeCommandAndInsertOnLogListWhenSaveIsInvoked() {
		Person person = createAPerson(30);
		person.save();
		assertEquals(1, CommandLogList.getLogList().size());
		assertEquals("Pessoa", Repository.getMapFor(Person.class).get(1L).getName());
		assertEquals(1L, person.getOID().longValue());
	}
	
	@Test
	public void shouldRemoveAPersonFromPeopleRepositoryAndCommandLogList(){
		Person person = createAPerson(30);
		person.save();
		assertEquals(1, Repository.getMapFor(Person.class).size());
		person.delete();
		assertEquals(0, Repository.getMapFor(Person.class).size());
		assertEquals(0, CommandLogList.getLogList().size());
	}
	
	@Test(expected = CannotRemovePrevalentObjectException.class)
	public void shouldRaiseExceptionWhenRemoveAPersonNotSavedYet(){
		Person person = createAPerson(30);
		person.delete(true);
	}
	
	@Test
	public void shouldNotRaiseExceptionWhenRemoveAPersonNotSavedYet(){
		Person person = createAPerson(30);
		person.delete();
	}
	
	@Test
	public void shouldRemoveAPersonFromPeopleRepositoryAndCommandLogListStaticWay(){
		Person person = createAPerson(30);
		person.save();
		assertEquals(1, Repository.getMapFor(Person.class).size());
		Person.delete(person);
		assertEquals(0, Repository.getMapFor(Person.class).size());
		assertEquals(0, CommandLogList.getLogList().size());
	}
	
	@Test
	public void shouldReturnAListOfSavedPeople() {
		Person p1 = createAPerson(30);
		Person p2 = createAPerson(30);
		p1.save();
		Person.save(p2);
		
		List<Person> people = Person.all();
		Assert.assertNotNull(people);
		Assert.assertEquals(2, people.size());
	}
	
	@Test
	public void shouldReturnAListOfSavedPeople2() {
		Person p1 = createAPerson(30);
		Person p2 = createAPerson(30);
		p1.save();
		Person.save(p2);
		
		List<Person> people = Person.all();
		Assert.assertNotNull(people);
		Assert.assertEquals(2, people.size());
		
		Person.delete(p1);
		
		people = Person.all();
		Assert.assertNotNull(people);
		Assert.assertEquals(1, people.size());
	}
	
	@Test
	public void shouldReturnEmptyListWhenNotExistisPersistedObjects() {
		List<Person> people = Person.all();
		Assert.assertNotNull(people);
	}
	
	@Ignore
	//TODO Verificar como salvar sub-classes de classes que estendem PrevalentObject
	public void shouldSaveACustomerAtRepository() {
		Customer customer = new Customer("Edipo", 23);
		customer.setAddress("Rua sem nome, Bairro Jardim Am�rica, 1231, Curitiba - PR");
		customer.setDtCadastro(new Date());
		customer.save();

		Assert.assertEquals(1, Repository.getListFor(Customer.class).size());
	}
	
	@Test
	public void shouldFindAPersonAtRepository() {
		Person p1 = createAPerson(19);
		Person p2 = createAPerson(50);
		Person child = createAPerson(5);
		Person child2 = createAPerson(11);
		Person p3 = createAPerson(18);
		p1.save(); p2.save(); p3.save();
		child.save(); child2.save();
		
		List<Person> adults = Finder.from(Person.class).getAll(new Filter<Person>() {
			public boolean accept(Person p) {
				return p.getAge() >= 18;
			}
		});
		Assert.assertEquals(3, adults.size());
	}
	
	@Test
	public void shouldFindAllPeople() {
		Person p = createAPerson(18);
		Person p2 = createAPerson(33);
		Person p3 = createAPerson(50);
		p.save(); p2.save(); p3.save();
		
		List<Person> people = Finder.from(Person.class).getAll();
		Assert.assertNotNull(people);
		Assert.assertEquals(3, people.size());
		Assert.assertEquals(50, people.get(2).getAge());
	}
	
	@Test
	public void shouldFindPersonByOID() {
		for (int i = 0; i < 1000; i++) {
			Person p = createAPerson(i);
			Person.save(p);
		}
		Person person662 = Finder.from(Person.class).getByOID(662L);
		Assert.assertNotNull(person662);
		Assert.assertEquals(661, person662.getAge());
	}
	
	@Test
	public void shouldMaintainSameReference() {
		Person p = createAPerson(18);
		p.save();
		Person anotherReferenceButSameObject = Finder.from(Person.class).getByOID(1L);
		Assert.assertSame(p, anotherReferenceButSameObject);
	}
	
	@Test
	public void shouldSaveAPerson() {
		Person p = createAPerson(30);
		p.save();
		Assert.assertEquals(1, Repository.getRepository().size());
	}
	
	@Test
	public void shouldMaintainOneCommandOnLogListWhenObjectStateIsUpdated() {
		Person person = createAPerson(30);
		person.save();
		assertEquals(1, CommandLogList.getLogList().size());
		assertEquals("Pessoa", Repository.getMapFor(Person.class).get(1L).getName());
		assertEquals(1L, person.getOID().longValue());
		
		person.setName("New name");
		person.save();
		assertEquals(1, CommandLogList.getLogList().size());
		assertEquals("New name", Repository.getMapFor(Person.class).get(1L).getName());
		assertEquals(1L, person.getOID().longValue());
	}

}