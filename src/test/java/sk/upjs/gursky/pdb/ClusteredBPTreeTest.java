package sk.upjs.gursky.pdb;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ClusteredBPTreeTest {

	private static final File INDEX_FILE = new File("person.kl");
	private ClusteredBPTree bptree;

	@Before
	public void setUp() throws Exception {
//		bptree = ClusteredBPTree.createOneByOne();
		bptree = ClusteredBPTree.createByBulkLoading();
	}

	@After
	public void tearDown() throws Exception {
		bptree.close();
		ClusteredBPTreeTest.INDEX_FILE.delete();
	}

	@Test
	public void testInsertAndRead() throws Exception {
		long time = System.currentTimeMillis();
		PersonEntry prev = null;
		int toPrint = 50;
		for (PersonEntry entry : bptree) {
			System.out.println(entry);
			if (prev != null) {
				assertTrue(prev.compareTo(entry) <= 0);
				assertTrue(prev.getKey().compareTo(entry.getKey()) <= 0);

			}
			prev = entry;
			if (toPrint == 0) break;
			toPrint--;
		}
		System.out.println(time);
	}

	@Test
	public void test() throws Exception {
		long time = System.currentTimeMillis();
		List<PersonEntry> result = bptree.intervalQuery(new PersonStringKey("a"), new PersonStringKey("b999999999"));
		time = System.currentTimeMillis() - time;

		System.out.println("!!!!!! Search time: " + time/1_000_000 +  " ms");

		assertTrue(result.size() > 0);
		int toPrint = 50;
		for (PersonEntry entry : result) {
			System.out.println(entry);
			if (toPrint == 0) break;
			toPrint--;
		}
	}
}
