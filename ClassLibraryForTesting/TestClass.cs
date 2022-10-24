namespace ClassLibraryForTesting
{
    public class TestClass
    {
        public void BuggyCode()
        {
            int target = -5;
            int num = 3;

            target = -num;
            target = +num;

            target = -num;
            target = +num;

            target = -num;
            target = +num;

            target = -num;
            target = +num;

            target = -num;
            target = +num;
        }

        public void DuplicateCode()
        {
            var x = 42;
            x = 42;
        }

        public int BuggyCodeBranch()
        {
            int target = -5;
            int num = 3;

            target = -num;
            target = +num;

            return target;
        }

        public int DuplicateCodeBranch()
        {
            var x = 42;
            x = 42;
            return x;

        }


    }
}