using Microsoft.AspNetCore.Mvc;

namespace WebApplicationForTesting.Controllers
{
    [ApiController]
    [Route("api/[controller]/[action]")]
    public class WeatherForecastController : ControllerBase
    {
        private static readonly string[] Summaries = new[]
        {
        "Freezing", "Bracing", "Chilly", "Cool", "Mild", "Warm", "Balmy", "Hot", "Sweltering", "Scorching"
    };


        public WeatherForecastController()
        {
        }

        [HttpGet(Name = "GetWeatherForecast")]
        public IEnumerable<WeatherForecast> Get()
        {
            return Enumerable.Range(1, 5).Select(index => new WeatherForecast
            {
                Date = DateTime.Now.AddDays(index),
                TemperatureC = Random.Shared.Next(-20, 55),
                Summary = Summaries[Random.Shared.Next(Summaries.Length)]
            })
            .ToArray();
        }

        [HttpGet(Name = "BuggyCodeBranch")]
        public int BuggyCodeBranch()
        {
            return new ClassLibraryForTesting.TestClass().BuggyCodeBranch();
        }

        [HttpGet(Name = "DuplicateCodeBranch")]
        public int DuplicateCodeBranch()
        {
            return new ClassLibraryForTesting.TestClass().DuplicateCodeBranch();
            return new ClassLibraryForTesting.TestClass().DuplicateCodeBranch();
            return new ClassLibraryForTesting.TestClass().DuplicateCodeBranch();
        }

        [HttpGet(Name = "DuplicateCodeBranch2")]
        public int DuplicateCodeBranch2()
        {
            return new ClassLibraryForTesting.TestClass().DuplicateCodeBranch();
            return new ClassLibraryForTesting.TestClass().DuplicateCodeBranch();
        }
    }
}