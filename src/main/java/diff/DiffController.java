package diff;

import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;

@RestController
public class DiffController {

    // TODO (vasi) (improvement): requests could be saved/retrived on/from a database

    private final HashMap<Long, String> left = new HashMap<>();
    private final HashMap<Long, String> right = new HashMap<>();

    // TODO (vasi) (improvement): create class Encoded, improves request body

    @PostMapping("/v1/diff/{id}/left")
    public Binary diffLeft(@PathVariable("id") Long id, @RequestBody String encoded) {
        if (!BinaryService.isBase64(encoded)){
            throw new InvalidBase64Exception();
        }
        Binary binary = BinaryService.extractBinaryFromEncoded(encoded);
        left.put(id, encoded);
        return binary;
    }

    @PostMapping("/v1/diff/{id}/right")
    public Binary diffRight(@PathVariable("id") Long id, @RequestBody String encoded) {
        if (!BinaryService.isBase64(encoded)){
            throw new InvalidBase64Exception();
        }

        Binary binary = BinaryService.extractBinaryFromEncoded(encoded);
        right.put(id, encoded);
        return binary;
    }

    @RequestMapping("/v1/diff/{id}")
    public String diff(@PathVariable("id") Long id) throws IOException {
        // TODO (vasi) (improvement): if left/right don't have the same id ?
        return DiffService.diff(left.get(id), right.get(id));
    }

}