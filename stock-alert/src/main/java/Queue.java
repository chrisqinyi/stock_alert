import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class Queue {
@Override
	public String toString() {
		// TODO Auto-generated method stub
		return ll.toString();
	}
public Queue(Integer size){
	this.size=size;
}
	private Integer size=5;
	
	private LinkedList<BigDecimal> ll = new LinkedList();

	public void put(Object[] o) {
		for (int i = 0; i < o.length; i++) {
			put((BigDecimal) o[i]);
		}
	}
	public void put(BigDecimal o) {
			ll.addLast(o);
			if(size<ll.size()){
				ll.removeFirst();
			}
	}
	// 使用removeFirst()方法，返回队列中第一个数据，然后将它从队列中删除
	public Object removeFirst() {
		return ll.removeFirst();
	}
	public Object removeLast() {
		return ll.removeLast();
	}
	

	public BigDecimal getLast() {
		return ll.getLast();
	}
	public boolean addAll(Collection<? extends BigDecimal> c) {
		return ll.addAll(c);
	}
	public boolean empty() {
		return ll.isEmpty();
	}
	public BigDecimal getAveragePrice(final Integer size) {
		BigDecimal result=BigDecimal.valueOf(0);
		if(ll.isEmpty()){
			return result;
		}
		Integer limit=ll.size()<size?ll.size():size;
		Integer index=0;
		for(Iterator<BigDecimal> it = ll.descendingIterator(); it.hasNext() && index < limit; index++) { 
			BigDecimal price=it.next();
			result=result.add(price);
		}
		result=result.divide(BigDecimal.valueOf(limit),2,RoundingMode.HALF_UP);
		return result;
	}
	public static void main(String[] args) {
		Queue mq = new Queue(15);

		BigDecimal[] str = { new BigDecimal(1), new BigDecimal(2), new BigDecimal(3), new BigDecimal(5),new BigDecimal(7),new BigDecimal(3),new BigDecimal(9) };
		mq.put(BigDecimal.valueOf(6));
		System.out.println(mq.getAveragePrice(1));
		mq.put(BigDecimal.valueOf(5));
		System.out.println(mq.getAveragePrice(2));
		mq.put(BigDecimal.valueOf(5));
		System.out.println(mq.getAveragePrice(2));
		mq.put(BigDecimal.valueOf(5));
		System.out.println(mq.getAveragePrice(2));
		mq.put(BigDecimal.valueOf(5));
		System.out.println(mq.getAveragePrice(2));
		mq.put(BigDecimal.valueOf(1));
		System.out.println(mq.getAveragePrice(15));

//		while (!mq.empty()) {
//			System.out.println(mq.get());
//		}

	}
}