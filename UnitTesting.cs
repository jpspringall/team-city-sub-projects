using FluentAssertions;
using Xunit;

namespace TCSonarCube
{
    public class UnitTesting
    {
        [Fact]
        public void Pass()
        {
            42.Should().Be(42);
        }

        [Fact]
        public void Fail()
        {
            42.Should().Be(42);
        }
    }
}