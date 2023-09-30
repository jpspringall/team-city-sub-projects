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
            1.Should().Be(1);

        }

        [Fact]
        public void Bug()
        {
            int target = -5;
            int num = 3;

            target = -num;  // Noncompliant; target = -3. Is that really what's meant?
            target = +num; // Noncompliant; target = 3
        }

        [Fact]
        public void WebApp()
        {
            3.Should().Be(new WebApplicationForTesting.Controllers.WeatherForecastController().BuggyCodeBranch());
        }
    }
}