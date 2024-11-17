package sk.upjs.gursky.pdb;

import java.io.Serial;
import java.nio.ByteBuffer;

import sk.upjs.gursky.bplustree.BPKey;

public class SalaryKey implements BPKey<SalaryKey> {

	@Serial
	private static final long serialVersionUID = 2381287865165519321L;
	private int key;
	// Integer

	public SalaryKey(int key) {
		this.key = key;
	}

	public SalaryKey() {
	}

	@Override
	public int compareTo(SalaryKey o) {
//		return this.key.compareTo(o.key);
		if (this.key < o.key) {
			return -1;
		}
		if (this.key > o.key) {
			return 1;
		} else { // (this.key == salaryKey.key)
			return 0;
		}
	}

	@Override
	public void load(ByteBuffer bb) {
		bb.getInt();
	}

	@Override
	public void save(ByteBuffer bb) {
		bb.putInt(this.key);
	}

	@Override
	public int getSize() {
		return 4; // salary je int = 4 B
	}

}
