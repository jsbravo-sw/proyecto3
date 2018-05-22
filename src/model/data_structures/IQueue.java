package model.data_structures;

public interface IQueue<E> {
	
	/** Enqueue a new element at the end of the queue */
	public void enqueue(E item);
	
	/** Dequeue the "first" element in the queue
	 * @return "first" element or null if it doesn't exist
	 */
	public E dequeue();
	
	/** Evaluate if the queue is empty. 
	 * @return true if the queue is empty. false in other case.
	 */
	public boolean isEmpty();
	
}
