package generated;

public class FailureInReturn {
	
	public Return test1() {
		null;
	}
	public Return test2() {
		return new Return();
	}
	public virtual class Return {
		public Return test1() {
			return null;
		}
		public Return test2() {
			return new Return();
		}
		public Return test3() {
			return new FailureInReturn().new Return();
		}
	}
}