package sk.upjs.gursky.pdb;

import java.io.Serial;
import java.nio.ByteBuffer;

import sk.upjs.gursky.bplustree.BPObject;

public class SalaryOffsetEntry implements BPObject<SalaryKey, SalaryOffsetEntry> {

	@Serial
	private static final long serialVersionUID = -7968085083056118868L;

	int salary;
	long offset;

	public SalaryOffsetEntry(Integer salary, long offset) {
		this.salary = salary;
		this.offset = offset;
	}

	public SalaryOffsetEntry() {
	}

	@Override
	public String toString() {
		return "SalaryOffsetEntry [salary=" + salary + ", offset=" + offset + "]";
	}

	@Override
	public int compareTo(SalaryOffsetEntry o) {
//		return this.salary.compareTo(o.salary);
		if (this.salary < o.salary) {
			return -1;
		}
		if (this.salary > o.salary) {
			return 1;
		} else { // (this.key == salaryKey.key)
			return 0;
		}
	}

	@Override
	public void load(ByteBuffer bb) {
		this.salary = bb.getInt();
		this.offset = bb.getLong();
	}

	@Override
	public void save(ByteBuffer bb) {
		bb.putInt(salary);
		bb.putLong(offset);
	}

	@Override
	public int getSize() {
		return 12; // lebo salary (4) + offset (8) = 12 B
	}

	@Override
	public SalaryKey getKey() {
		return new SalaryKey(salary);
	}

}
