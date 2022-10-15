namespace ClassLibraryForTesting
{
    public class TestClass
    {
        public void BuggyCode()
        {
            int target = -5;
            int num = 3;

            target = -num;  // Noncompliant; target = -3. Is that really what's meant?
            target = +num; // Noncompliant; target = 3
        }

        public void DuplicateCode()
        {
            var x = 42;
            x = 42;
            x = 42;
            x = 42;
            x = 42;
            x = 42;
            x = 42;
            x = 42;
            x = 42;
            x = 42;// Noncompliant; target = 3
        }


    }
}