package sk.upjs.gursky.pdb;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sk.upjs.gursky.bplustree.BPTree;

public class ClusteredBPTree extends BPTree<PersonStringKey, PersonEntry> {

	private static final long serialVersionUID = 8959794941026592726L;
	
	public static final File INDEX_FILE = new File("person.tree");
	public static final File INPUT_DATA_FILE = new File("person.tab");

	private ClusteredBPTree() throws IOException{
		super(PersonEntry.class, INDEX_FILE);
	}

	public static ClusteredBPTree createOneByOne() throws IOException {
		long startTime = System.nanoTime();
		ClusteredBPTree tree = new ClusteredBPTree();
		tree.openNewFile();

		RandomAccessFile raf = new RandomAccessFile(INPUT_DATA_FILE, "r");

		FileChannel channel = raf.getChannel();
		ByteBuffer buffer = ByteBuffer.allocateDirect(4096); // velkost stranky
		

		long fileSize = INPUT_DATA_FILE.length();
		for (int offset = 0; offset < fileSize; offset += 4096) {
			System.out.println("processing page " + (offset / 4096));
			buffer.clear(); // vyprazdni buffer a da nam sipku na zaciatok buffra
			channel.read(buffer, offset);
			buffer.rewind(); // previniem "pasku" na zaciatok a mozem z neho citat
			int numberOfRecords = buffer.getInt(); // precita si 4 bajty z buffra, sipka sa tam prevynie
			for (int i = 0; i < numberOfRecords; i++) {
				PersonEntry entry = new PersonEntry();
				entry.load(buffer);
				tree.add(entry); // zapise prvok podla nasho pracneho algoritmu z cvicenia
			}
		}
		channel.close();
		raf.close();
		System.out.println("Index created in " + (System.nanoTime() - startTime)/1_000_000.0 + " ms");
		return tree;
	}

	public static ClusteredBPTree createByBulkLoading() throws IOException {
		long startTime = System.nanoTime();
		ClusteredBPTree tree = new ClusteredBPTree();
//		tree.openNewFile(); // zmazane pre bulk loading

		RandomAccessFile raf = new RandomAccessFile(INPUT_DATA_FILE, "r");

		FileChannel channel = raf.getChannel();
		ByteBuffer buffer = ByteBuffer.allocateDirect(4096); // velkost stranky
		
		// pridane pre bulk loading
		List<PersonEntry> entries = new ArrayList<PersonEntry>();
		//

		long fileSize = INPUT_DATA_FILE.length();
		for (int offset = 0; offset < fileSize; offset += 4096) {
			System.out.println("processing page " + (offset / 4096));
			buffer.clear(); // vyprazdni buffer a da nam sipku na zaciatok buffra
			channel.read(buffer, offset);
			buffer.rewind(); // previniem "pasku" na zaciatok a mozem z neho citat
			int numberOfRecords = buffer.getInt(); // precita si 4 bajty z buffra, sipka sa tam prevynie
			for (int i = 0; i < numberOfRecords; i++) {
				PersonEntry entry = new PersonEntry();
				entry.load(buffer);
//				tree.add(entry); // zapise prvok podla nasho pracneho algoritmu z cvicenia
				entries.add(entry);
			}
		}
		// pridane pre bulk loading
		Collections.sort(entries);
		tree.openAndBatchUpdate(entries.iterator(), entries.size());
		//
		channel.close();
		raf.close();
		System.out.println("Index created in " + (System.nanoTime() - startTime)/1_000_000.0 + " ms");
		return tree;

	}

}
