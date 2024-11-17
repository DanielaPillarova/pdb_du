package sk.upjs.gursky.pdb;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import sk.upjs.gursky.bplustree.BPTree;

public class UnclusteredBPTree extends BPTree<PersonStringKey, SurnameAndOffsetEntry> {

	private static final long serialVersionUID = 7246748234189737254L;

	public static final File INDEX_FILE = new File("person.unclustered.tree");
	public static final File INPUT_DATA_FILE = new File("person.tab");

	public UnclusteredBPTree() throws IOException {
		super(SurnameAndOffsetEntry.class, INDEX_FILE);
	}

	public static UnclusteredBPTree createByBulkLoading() throws IOException {
		long startTime = System.nanoTime();
		UnclusteredBPTree tree = new UnclusteredBPTree();

		RandomAccessFile raf = new RandomAccessFile(INPUT_DATA_FILE, "r");

		FileChannel channel = raf.getChannel();
		ByteBuffer buffer = ByteBuffer.allocateDirect(4096); // velkost stranky

		List<SurnameAndOffsetEntry> entries = new ArrayList<>();

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
				long entryOffset = offset + 4 + i * entry.getSize(); // ak mame 1. zaznam je to offset + 4 atd..
				SurnameAndOffsetEntry item = new SurnameAndOffsetEntry(entry.surname, entryOffset);
				entries.add(item);
			}
		}
		Collections.sort(entries);
		tree.openAndBatchUpdate(entries.iterator(), entries.size());
		channel.close();
		raf.close();
		System.out.println("Index created in " + (System.nanoTime() - startTime) / 1_000_000.0 + " ms");
		return tree;
	}

	public List<PersonEntry> unclusteredIntervalQuery(PersonStringKey low, PersonStringKey high) throws IOException {
		List<SurnameAndOffsetEntry> references = intervalQuery(low, high);

		RandomAccessFile raf = new RandomAccessFile(INPUT_DATA_FILE, "r");

		FileChannel channel = raf.getChannel();
		ByteBuffer buffer = ByteBuffer.allocateDirect(4096);

		List<PersonEntry> result = new LinkedList<>();
		long lastOffset = -1L;
		int accesses = 0;
		for (SurnameAndOffsetEntry ref : references) {
			// vypocitat kde je zaciatok stranky
			// do bytebuffra dam celu stranku
			// previniem sa na spravnu poziciu -> .position(int newPosition)
			long pageOffset = (ref.offset / 4096) * 4096;
			if (lastOffset != pageOffset) {
				lastOffset = pageOffset;
				buffer.clear();
				channel.read(buffer, pageOffset);
				accesses++;
			}
			int entryInPageOffset = (int) (ref.offset - pageOffset);
			buffer.position(entryInPageOffset);
			PersonEntry entry = new PersonEntry();
			entry.load(buffer);
			result.add(entry);
		}

		channel.close();
		raf.close();
		System.out.println("I/O operatons: " + accesses);
		return result;
	}

}
