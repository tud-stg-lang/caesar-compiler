package weaving.aspects;

deployed public cclass AspectA {
	after() : execution(* *..Base*.m(..)) {
		System.out.println("after Base*.m");
	}
}
