package sk.upjs.gursky.pdb;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UnclusteredBPTreeSalaryTest {

	private UnclusteredBPTreeSalary bptree;

	@Before
	public void setUp() throws Exception {
		bptree = UnclusteredBPTreeSalary.createByBulkLoading();
	}

	@After
	public void tearDown() throws Exception {
		bptree.close();
		UnclusteredBPTreeSalary.INDEX_FILE.delete();
	}

	@Test
	public void test() throws Exception {
		long time = System.nanoTime();
		List<PersonEntry> result = bptree.unclusteredIntervalQuerySalary(new SalaryKey(400), new SalaryKey(800));
		time = System.nanoTime() - time;

		System.out.println("Interval unclusteres: " + time / 1_000_000.0 + " ms");

		for (int i = 0; i < 20; i++) {
			System.out.println("Name: " + result.get(i).name + ", surname: " + result.get(i).surname + ", age: "
					+ result.get(i).age + ", salary: " + result.get(i).salary);
		}
		for (PersonEntry personEntry : result) {
			assertTrue(personEntry.salary >= 400 && personEntry.salary <= 800);
		}

	}

}
